package code.sort;


import java.util.Arrays;
import java.util.Random;

/**
 * @Author lisenmiao
 * @Date 2020/12/23 10:42
 */
public class SortUtils {
    public static void compareSortResult(){
        int len = 1001;
        int[] arr = new int[len];
        int[] myArr = new int[len];
        Random random = new Random(0);
        for (int i = 0; i < len; i++) {
            int nextInt = random.nextInt(len);
            arr[i] = nextInt;
        }
        System.arraycopy(arr,0,myArr,0,len);
        Arrays.sort(arr);
//        bubbleSortV3(myArr);
//        selectionSort(myArr);
//        selectionSortV2(myArr);
//        heapSort(myArr);
        mergeSort(myArr);
        boolean result = true;
        for (int i = 0; i < myArr.length; i++) {
            if(myArr[i]!=arr[i]){
                result = false;
                break;
            }
        }
        System.out.println("对比结果:" + result);
    }

    /**
     * 原始冒泡，从左往右，每次循环选出最大的
     * @param arr
     */
    public static void bubbleSort(int[] arr){
        for (int i = 0; i < arr.length-1; i++) {
            for (int j = 0; j < arr.length-i-1; j++) {
                if(arr[j]>arr[j+1]){
                    swap(arr,j,j+1);
                }
            }
            printArr(arr);
        }
    }

    /**
     * 优化1：如果某次循环没有进行交换，则说明数组已经有序，不再进行对比
     * @param arr
     */
    public static void bubbleSortV2(int[] arr){
        for (int i = 0; i < arr.length-1; i++) {
            // 设定一个标记，若为true，则表示此次循环没有进行交换，也就是待排序列已经有序，排序已经完成。
            boolean swaped = false;
            for (int j = 0; j < arr.length-i-1; j++) {
                if(arr[j]>arr[j+1]){
                    swap(arr,j,j+1);
                    swaped = true;
                }
            }
            System.out.println("第" + (i+1) + "次排序");
            printArr(arr);
            if(!swaped){
                break;
            }
        }
    }

    /**
     * 优化1加上
     * 优化2：
     * 进行两次筛选，
     * 从左到右选出最大的，同时从右往左选出最小的
     *
     * @param arr
     */
    public static void bubbleSortV3(int[] arr){
        for (int i = 0; i < arr.length-1; i++) {
            // 设定一个标记，若为true，则表示此次循环没有进行交换，也就是待排序列已经有序，排序已经完成。
            boolean swaped = false;
            //int j = i 由于经过第i次筛选后，前i个是最小的，后i个是最大的，故每次循环从i开始
            for (int j = i; j < arr.length-i-1; j++) {
                if(arr[j]>arr[j+1]){
                    swap(arr,j,j+1);
                    swaped = true;
                }
            }
            for (int j = arr.length-1-i-1; j >i; j--) {
                if(arr[j]<arr[j-1]){
                    swap(arr,j,j-1);
                    swaped = true;
                }
            }
            System.out.println("第" + (i+1) + "次排序");
            printArr(arr);
            if(!swaped){
                break;
            }
        }
    }

    public static void swap(int[] arr,int index1, int index2){
        if(index1 == index2){
            return;
        }
        int tmp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = tmp;
    }
    public static void printArr(int[] arr){
        for (int value : arr) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    /**
     * 选择排序：每次遍历选出最大的元素放在最后面
     * @param arr
     */
    public static void selectionSort(int[] arr){
        for (int i = 0; i < arr.length; i++) {
            int maxIndex = 0;
            for (int j = 1; j < arr.length-i; j++) {
                if(arr[maxIndex]<arr[j]){
                    maxIndex = j;
                }
            }
            swap(arr,maxIndex,arr.length-i-1);
            System.out.println("第" + (i+1) + "次排序");
            printArr(arr);
        }
    }
    /**
     * 选择排序优化1：
     * 每次遍历选出最大的元素放在最后面
     * 并且选出最小元素放在最前面
     * @param arr
     */
    public static void selectionSortV2(int[] arr){
        for (int i = 0; i < arr.length/2; i++) {
            int maxIndex = i;
            int minIndex = i;
            boolean flag = false;
            for (int j = i+1; j < arr.length-i; j++) {
                if(arr[maxIndex]<arr[j]){
                    maxIndex = j;
                }else {
                    flag = true;
                }
            }
            swap(arr,maxIndex,arr.length-i-1);
            for (int j = arr.length-i-1-1; j >i; j--) {
                if(arr[minIndex]>arr[j]){
                    minIndex = j;
                    flag = true;
                }
            }
            swap(arr,minIndex,i);
            System.out.println("第" + (i+1) + "次排序");
            printArr(arr);
            if(!flag){
                break;
            }
        }
    }
    /**
     * 堆排序
     * @param arr
     */
    public static void heapSort(int[] arr) {
        //构造大根堆
        heapInsert(arr);
        int size = arr.length;
        while (size > 1) {
            //固定最大值
            swap(arr, 0, size - 1);
            size--;
            //构造大根堆
            heapify(arr, 0, size);

        }

    }


    //构造大根堆（通过新插入的数上升）
    public static void heapInsert(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            //当前插入的索引
            int currentIndex = i;
            //父结点索引
            int fatherIndex = (currentIndex - 1) / 2;
            //如果当前插入的值大于其父结点的值,则交换值，并且将索引指向父结点
            //然后继续和上面的父结点值比较，直到不大于父结点，则退出循环
            while (arr[currentIndex] > arr[fatherIndex]) {
                //交换当前结点与父结点的值
                swap(arr, currentIndex, fatherIndex);
                //将当前索引指向父索引
                currentIndex = fatherIndex;
                //重新计算当前索引的父索引
                fatherIndex = (currentIndex - 1) / 2;
            }
        }
    }

    /**
     *
     * @param arr
     * @param index
     * @param size
     */
    //将剩余的数构造成大根堆（通过顶端的数下降）
    public static void heapify(int[] arr, int index, int size) {
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        while (left < size) {
            int largestIndex;
            //判断孩子中较大的值的索引（要确保右孩子在size范围之内）
            if (arr[left] < arr[right] && right < size) {
                largestIndex = right;
            } else {
                largestIndex = left;
            }
            //比较父结点的值与孩子中较大的值，并确定最大值的索引
            if (arr[index] > arr[largestIndex]) {
                largestIndex = index;
            }
            //如果父结点索引是最大值的索引，那已经是大根堆了，则退出循环
            if (index == largestIndex) {
                break;
            }
            //父结点不是最大值，与孩子中较大的值交换
            swap(arr, largestIndex, index);
            //将索引指向孩子中较大的值的索引
            index = largestIndex;
            //重新计算交换之后的孩子的索引
            left = 2 * index + 1;
            right = 2 * index + 2;
        }

    }

    public static void mergeSort(int [] arr){
        mergeSortProcess(arr, 0,arr.length-1);
    }
//2,4,6,3,1,7,9,8,5,0
    public static void mergeSortProcess(int[] arr, int L, int R){
        if(L==R){
            return;
        }
        int M = L+ ((R-L)>>1);
        mergeSortProcess(arr, L,M);
        mergeSortProcess(arr, M+1,R);
        merge(arr,L,M+1,R);
    }
    public static void merge(int[] arr,int L,int M ,int R){
        int[] newArr = new int[R-L+1];
        int i=0;
        int p = L;
        int q = M;
        while (p<M && q<=R){
            if(arr[p]<=arr[q]){
                newArr[i] = arr[p];
                p++;
            }else {
                newArr[i] = arr[q];
                q++;
            }
            i++;
        }

        while (p<M){
            newArr[i++] = arr[p++];
        }
        while (q<=R){
            newArr[i++] = arr[q++];
        }
        System.arraycopy(newArr,0,arr,L,R-L+1);
    }
    public static void main(String[] args) {
        int []arr = new int[]{2,4,6,3,1,7,9,8,5,0};
        System.out.println("起始数组：");
        printArr(arr);
//        bubbleSort(arr);
//        bubbleSortV2(arr);
//        bubbleSortV3(arr);
//        selectionSort(arr);
//        selectionSortV2(arr);
//        heapSort(arr);
        mergeSort(arr);
        System.out.println("最终数组：");
        printArr(arr);
        compareSortResult();
    }
}
