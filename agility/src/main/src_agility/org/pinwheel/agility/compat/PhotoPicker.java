package org.pinwheel.agility.compat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import org.pinwheel.agility.util.callback.Action2;

import java.io.File;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public final class PhotoPicker {

    private static final int CODE_TAKE_PHOTO = 3728;
    private static final int CODE_PICK_PHOTO = 3729;

    public enum Type {
        TAKE_PHOTO, PICK_PHOTO
    }

    public enum Error {
        NO_TAKE, NO_PICK, TAKE_PATH_ERR, PICK_PATH_ERR, UNKNOWN
    }

    private final Activity activity;
    private final File path;
    private final String fileProviderAuthorities;
    private Action2<File, Error> listener;

    public PhotoPicker(Activity activity) {
        // default path: /Android/data/packageName/cache/picker
        // default authorities: packageName.fileprovider
        this(activity, new File(activity.getExternalCacheDir(), "picker"), activity.getPackageName() + ".fileprovider");
    }

    public PhotoPicker(Activity activity, String fileProviderAuthorities) {
        // default path: /Android/data/packageName/cache/picker
        this(activity, new File(activity.getExternalCacheDir(), "picker"), fileProviderAuthorities);
    }

    public PhotoPicker(Activity activity, File path, String fileProviderAuthorities) {
        this.activity = activity;
        this.path = path;
        this.fileProviderAuthorities = fileProviderAuthorities;
    }

    public PhotoPicker setOnPickListener(Action2<File, Error> listener) {
        this.listener = listener;
        return this;
    }

    public void show(Type type) {
        if (type == Type.TAKE_PHOTO) {
            takePhoto();
        } else if (type == Type.PICK_PHOTO) {
            pickPhoto();
        }
    }

    private void pickPhoto() {
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType("image/*");
        activity.startActivityForResult(getAlbum, CODE_PICK_PHOTO);
    }

    private void takePhoto() {
        final String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                if (!path.exists()) {
                    path.mkdirs();
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File outFile = new File(path, String.valueOf(System.nanoTime()) + ".png");
                if (outFile.exists()) {
                    outFile.delete();
                }
                // 适配 N
                final Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    uri = FileProvider.getUriForFile(activity.getApplicationContext(), fileProviderAuthorities, outFile);
                } else {
                    uri = Uri.fromFile(outFile);
                }
                // intent中指定路径，否则随intent中返回的数据是被压缩过后的
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(intent, CODE_TAKE_PHOTO);
                tmpFile = outFile;
            } catch (ActivityNotFoundException e) {
                notifyError(Error.TAKE_PATH_ERR);
            }
        } else {
            notifyError(Error.TAKE_PATH_ERR);
        }
    }

    private File tmpFile = null;

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case CODE_TAKE_PHOTO: {
                final File outFile = tmpFile;
                if (Activity.RESULT_OK == resultCode && null != outFile && outFile.exists() && outFile.canRead()) {
                    notifySuccess(outFile);
                } else {
                    notifyError(Error.NO_TAKE);
                }
                tmpFile = null;
            }
            break;
            case CODE_PICK_PHOTO: {
                if (data != null) {
                    String path = "";
                    Uri uri = getUri(activity, data);
                    try {
                        // 先从数据库中获取URI对应的路径
                        String[] filePathColumns = {MediaStore.Images.Media.DATA};
                        Cursor c = activity.getContentResolver().query(uri, filePathColumns, null, null, null);
                        if (c != null && c.moveToFirst()) {
                            int columnIndex = c.getColumnIndex(filePathColumns[0]);
                            path = c.getString(columnIndex);
                            if (isGooglePhotosUri(uri)) {
                                path = uri.getLastPathSegment();
                            } else if (isMediaDocument(uri) && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                final String docId = DocumentsContract.getDocumentId(uri);
                                final String[] split = docId.split(":");
                                final String type = split[0];
                                Uri contentUri = null;
                                if ("image".equals(type)) {
                                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                } else if ("video".equals(type)) {
                                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                } else if ("audio".equals(type)) {
                                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                }
                                final String selection = "_id=?";
                                final String[] selectionArgs = new String[]{split[1]};
                                path = getDataColumn(activity, contentUri, selection, selectionArgs);
                            }
                        }
                        if (c != null) {
                            c.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (uri != null) {
                            path = uri.getPath();
                        }
                    }

                    if (!TextUtils.isEmpty(path)) {
                        final File file = new File(path);
                        notifySuccess(file);
                    } else {
                        notifyError(Error.PICK_PATH_ERR);
                    }
                } else {
                    notifyError(Error.NO_PICK);
                }
            }
            break;
        }
    }

    private void notifyError(Error error) {
        if (null != listener) {
            listener.call(null, error);
        }
    }

    private void notifySuccess(File imgFile) {
        if (null != listener) {
            listener.call(imgFile, null);
        }
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 兼容小米手机
     */
    private static Uri getUri(Context context, Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver contentResolver = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

}