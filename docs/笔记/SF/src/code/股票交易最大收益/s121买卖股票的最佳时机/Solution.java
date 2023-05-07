package code.股票交易最大收益.s121买卖股票的最佳时机;


/**
 * 121. 买卖股票的最佳时机
 * 给定一个数组 prices ，它的第 i 个元素 prices[i] 表示一支给定股票第 i 天的价格。
 *
 * 你只能选择 某一天 买入这只股票，并选择在 未来的某一个不同的日子 卖出该股票。设计一个算法来计算你所能获取的最大利润。
 *
 * 返回你可以从这笔交易中获取的最大利润。如果你不能获取任何利润，返回 0 。
 *
 *
 *
 * 示例 1：
 *
 * 输入：[7,1,5,3,6,4]
 * 输出：5
 * 解释：在第 2 天（股票价格 = 1）的时候买入，在第 5 天（股票价格 = 6）的时候卖出，最大利润 = 6-1 = 5 。
 *      注意利润不能是 7-1 = 6, 因为卖出价格需要大于买入价格；同时，你不能在买入前卖出股票。
 * 示例 2：
 *
 * 输入：prices = [7,6,4,3,1]
 * 输出：0
 * 解释：在这种情况下, 没有交易完成, 所以最大利润为 0。
 * https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock/solution/121-mai-mai-gu-piao-de-zui-jia-shi-ji-by-leetcode-/
 *
 * https://zhuanlan.zhihu.com/p/92908822
 *
 * https://leetcode-cn.com/circle/article/qiAgHn/
 */
class Solution {
//    [7,1,5,3,6,4]
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int i = 0; i < prices.length; i++) {
            int p = prices[i];
            minPrice = Math.min(minPrice,p);
            maxProfit = Math.max(maxProfit,p-minPrice);
//            if(p<minPrice){
//                minPrice = p;
//            }else if(p-minPrice>maxProfit){
//                maxProfit = p-minPrice;
//            }
        }
        return maxProfit;
    }

    public static void main(String[] args) {
        int[] p = new int[]{7,2,15,1,5,3,6,4};
        int maxProfit = new Solution().maxProfit(p);
        System.out.println(maxProfit);
    }
}