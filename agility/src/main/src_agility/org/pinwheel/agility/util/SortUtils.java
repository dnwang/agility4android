package org.pinwheel.agility.util;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class SortUtils {

    private SortUtils() {

    }

    public static enum Order {
        DESC, ASC
    }

    public static void insertSort(long[] arr) {
        int size = arr.length;
        for (int i = 1; i < size; i++) {
            long temp = arr[i];
            int j = i;
            for (j = i; j > 0; j--) {
                if (arr[j - 1] > temp) {
                    arr[j] = arr[j - 1];
                } else {
                    break;
                }
            }
            arr[j] = temp;
        }
    }

    public static void bubbleSort(long[] arr) {
        int size = arr.length;
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < arr.length - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    long temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void selectionSort(Order order, long[] arr) {
        int size = arr.length;
        for (int i = 0; i < size - 1; ++i) {
            int k = i;
            for (int j = i; j < arr.length; ++j) {
                if (order == Order.ASC && arr[k] > arr[j]) {
                    k = j;
                } else if (arr[k] < arr[j]) {
                    k = j;
                }
            }
            if (k != i) {
                long temp = arr[i];
                arr[i] = arr[k];
                arr[k] = temp;
            }
        }
    }

    public static void quickSort(int arr[], int low, int high) {
        if (low < high) {
            int i, j, x;
            i = low;
            j = high;
            x = arr[i];
            while (i < j) {
                while (i < j && arr[j] > x) {
                    j--;
                }
                if (i < j) {
                    arr[i] = arr[j];
                    i++;
                }
                while (i < j && arr[i] < x) {
                    i++;
                }
                if (i < j) {
                    arr[j] = arr[i];
                    j--;
                }
            }
            arr[i] = x;
            quickSort(arr, low, i - 1);
            quickSort(arr, i + 1, high);
        }
    }

}
