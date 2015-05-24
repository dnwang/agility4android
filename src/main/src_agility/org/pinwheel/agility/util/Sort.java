package org.pinwheel.agility.util;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;

/**
 * 排序算法
 * @author WangDenan
 *
 */
public final class Sort {

    private Sort(){

    }

	public final static int DESC = -1 ;
	public final static int ACS = 1 ;
	
	/**
	 * 插入排序
	 * @param arr
	 */
	public static void insertSort(long[] arr) {
		int size = arr.length ;
		for (int i = 1; i < size; i++) {
			long temp = arr[i];
			int j = i;
			// 寻找 插入位置
			for (j = i; j > 0; j--) {
				if (arr[j - 1] > temp) {
					arr[j] = arr[j - 1];
				} else {
					// 如果 当前数 已经 小于 temp 则说明 前面所有数均小于temp
					break;
				}
			}
			arr[j] = temp;
		}
	}

	/**
	 * 冒泡排序
	 * 
	 * @param arr
	 */
	public static void bubbleSort(long[] arr) {
		int size = arr.length ;
		for (int i = 1; i < size; i++) { // 数组有多长,轮数就有多长
			// 将相邻两个数进行比较，较大的数往后冒泡
			for (int j = 0; j < arr.length - i; j++) {// 每一轮下来会将比较的次数减少
				if (arr[j] > arr[j + 1]) {
					// 交换相邻两个数
					long temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;
				}
			}
		}
	}

	/**
	 * 选择排序法
	 * @param order 1:递增,-1:递减
	 * @param arr
	 */
	public static void selectionSort(int order, long[] arr) {
		int size = arr.length ;
		for (int i = 0; i < size - 1; ++i) {
			int k = i;
			// 选择要交换的位置
			for (int j = i; j < arr.length; ++j) {
				if (order == 1 && arr[k] > arr[j]) {
					k = j;
				}else if (arr[k] < arr[j]){
					k = j;
				}
			}
			// 交换元素
			if (k != i) {
				long temp = arr[i];
				arr[i] = arr[k];
				arr[k] = temp;
			}
		}
	}

	/**
	 * 快速排序
	 * @param arr
	 * @param low
	 * @param high
	 */
	public static void quickSort(int arr[], int low, int high) {
		if (low < high) { // 结束递归条件
			int i, j, x;
			i = low;
			j = high;
			x = arr[i];
			while (i < j) {
				while (i < j && arr[j] > x) {
					j--; // 从右向左找第一个小于x的数
				}
				if (i < j) {
					arr[i] = arr[j];
					i++;
				}
				while (i < j && arr[i] < x) {
					i++; // 从左向右找第一个大于x的数
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

	/**
	 * 
	 * @param order 1:递增, -1:递减
	 * @param JSONArray
	 * @param key
	 * @return  根据Key排序过后的index序列
	 * @throws Exception
	 */
	public static int[] sortJSONArray(int order, JSONArray jsons, String key) throws Exception{
		final int size = jsons.length() ;
		Map<Long, Integer> id2index = new HashMap<Long, Integer>() ;
		long[] ids = new long[size] ;
		for (int i = 0; i < size; i++) {
			long id = jsons.getJSONObject(i).getLong(key) ;
			id2index.put(id, i) ;
			ids[i] = id ;
		}
		selectionSort(order, ids) ;
		int[] indexs = new int[size] ;
		for (int i = 0; i < size; i++) {
			indexs[i] = id2index.get(ids[i]) ;
		}
		return indexs ;
	}
}
