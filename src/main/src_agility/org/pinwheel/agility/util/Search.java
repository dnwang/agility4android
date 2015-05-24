package org.pinwheel.agility.util;

/**
 * 查找算法
 *
 * @author WangDenan
 */
public final class Search {

    private Search(){

    }

    /**
     * 二分查找
     *
     * @param arr
     * @param value
     * @return index
     */
    public static int binarySearch(int arr[], int value) {
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (arr[mid] == value) {
                return mid;
            } else if (arr[mid] > value) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }
}
