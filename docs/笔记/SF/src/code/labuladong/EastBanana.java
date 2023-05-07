package code.labuladong;

/**
 * 二分查找
 * https://github.com/labuladong/fucking-algorithm/blob/master/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E7%B3%BB%E5%88%97/koko%E5%81%B7%E9%A6%99%E8%95%89.md
 * @Author lisenmiao
 * @Date 2021/1/22 17:12
 */
public class EastBanana {

    int getMax(int[] piles){
        int max = 0;
        for (int i = 0; i < piles.length; i++) {
            max = Math.max(max,piles[i]);
        }
        return max;
    }

    /**
     * 遍历的方式
     * @param piles
     * @param H
     * @return
     */
    int minEatingSpeed1(int[] piles,int H){
        int maxCount = getMax(piles);
        for (int speed = 1; speed < maxCount; speed++) {
            if(canEat(speed,piles,H)){
                return speed;
            }
        }
        return maxCount;
    }

    /**
     * 二分法
     * @param piles
     * @param H
     * @return
     */
    int minEatingSpeed(int[] piles,int H){
        int right = getMax(piles);
        int left = 1;
        while ( left < right ) {
            int mid = left + ((right - left)>>1);
            if(canEat(mid,piles,H)){
                right = mid;
            }else {
               left = mid+1;
            }
        }
        return left;
    }

    private boolean canEat(int speed, int[] piles, int H) {
        int totalHours = 0;
        for (int i = 0; i < piles.length; i++) {
            int pile = piles[i];
            if(pile>speed){
                totalHours += (pile/speed);
                if(pile%speed!=0){
                    totalHours+=1;
                }
            }else {
                totalHours+=1;
            }
        }
        return totalHours<=H;
    }

    public static void main(String[] args) {
        int [] piles = new int[]{1,2,2,4,3};
        int H = 5;
        EastBanana eastBanana = new EastBanana();
        int minSpread = eastBanana.minEatingSpeed(piles, 6);
        System.out.println(minSpread);
    }
}
