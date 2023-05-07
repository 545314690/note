package code.sl.s4;

class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if(((nums1==null)||nums1.length==0) && ((nums2==null)||nums2.length==0) ){
            return 0;
        }
        int i=0;
        int j=0;
        int[] newArr = new int[nums1.length+nums2.length];
        while (i<nums1.length || j<nums2.length){
            if(i==nums1.length){
                newArr[i+j] = nums2[j];
                j++;
                continue;
            }else if(j==nums2.length){
                newArr[i+j] = nums1[i];
                i++;
                continue;
            }
            int min = Math.min(nums1[i],nums2[j]);
            newArr[i+j] = min;
            if(nums1[i]<nums2[j]){
                i++;
            }else {
                j++;
            }
        }
        if(newArr.length%2==0){
            int a = newArr[(nums1.length+nums2.length)/2-1];
            int b = newArr[(nums1.length+nums2.length)/2];
            return (a+b)/2.0;
        }else {
            return newArr[(nums1.length+nums2.length)/2];
        }
    }

    public static void main(String[] args) {
        int[] a = new int[]{1,2};
        int[] b = new int[]{3,4};

        double medianSortedArrays = new Solution().findMedianSortedArrays(a, b);
        System.out.println(medianSortedArrays);
    }
}