package code.股票交易最大收益.s123买卖股票的最佳时机III;

/**
 * 给定一个数组，它的第 i 个元素是一支给定的股票在第 i 天的价格。
 *
 * 设计一个算法来计算你所能获取的最大利润。你最多可以完成 两笔 交易。
 *
 * 注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
 *
 *  
 *
 * 示例 1:
 *
 * 输入：prices = [3,3,5,0,0,3,1,4]
 * 输出：6
 * 解释：在第 4 天（股票价格 = 0）的时候买入，在第 6 天（股票价格 = 3）的时候卖出，这笔交易所能获得利润 = 3-0 = 3 。
 *      随后，在第 7 天（股票价格 = 1）的时候买入，在第 8 天 （股票价格 = 4）的时候卖出，这笔交易所能获得利润 = 4-1 = 3 。
 * 示例 2：
 *
 * 输入：prices = [1,2,3,4,5]
 * 输出：4
 * 解释：在第 1 天（股票价格 = 1）的时候买入，在第 5 天 （股票价格 = 5）的时候卖出, 这笔交易所能获得利润 = 5-1 = 4 。  
 *      注意你不能在第 1 天和第 2 天接连购买股票，之后再将它们卖出。  
 *      因为这样属于同时参与了多笔交易，你必须在再次购买前出售掉之前的股票。
 * 示例 3：
 *
 * 输入：prices = [7,6,4,3,1]
 * 输出：0
 * 解释：在这个情况下, 没有交易完成, 所以最大利润为 0。
 * 示例 4：
 *
 * 输入：prices = [1]
 * 输出：0
 *
 * 链接：https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-iii
 */
class Solution {
    public int maxProfit(int[] prices) {
        /**
         对于任意一天考虑四个变量:
         fstBuy: 在该天第一次买入股票的成本
         fstSell: 在该天第一次卖出股票可获得的最大收益
         secBuy: 在该天第二次买入股票的总成本（包括之前的一次买卖收益）
         secSell: 在该天第二次卖出股票可获得的最大收益
         分别对四个变量进行相应的更新, 最后secSell就是最大
         收益值(secSell >= fstSell)
         **/
        int fstBuy = Integer.MAX_VALUE, fstSell = 0;
        int secBuy = Integer.MAX_VALUE, secSell = 0;
        for(int p : prices) {
            fstBuy = Math.min(fstBuy, p); //第一次买成本最小
            fstSell = Math.max(fstSell, p-fstBuy); //第一次卖收益最大
            secBuy = Math.min(secBuy, p-fstSell); ////第二次买总成本最小。。。p-fstSell是用第一次的钱抵消了一部分第二次买的钱。
            secSell = Math.max(secSell,  p-secBuy);//第二次卖收益最大
        }
        return secSell;



//        int buy1 = Integer.MAX_VALUE;
//        int buy2 = Integer.MAX_VALUE;
//        int sell1 = 0;
//        int sell2 = 0;
//        for (int p: prices ) {
//            buy1 = Math.min(buy1,p);
//            sell1 = Math.max(sell1,p-buy1);
//            buy2 = Math.min(buy2,p-sell1);
//            sell2 = Math.max(sell2,p-buy2);
//        }
//        return sell2;
    }

    public static void main(String[] args) {
        int[] prices = new int[]{3,3,5,0,0,3,1,4};
        int maxProfit = new Solution().maxProfit(prices);
        System.out.println(maxProfit);
    }
}