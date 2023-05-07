package code.linklist.s25K个一组翻转链表;

import java.util.Stack;

/**
 * @Author lisenmiao
 * @Date 2021/1/25 11:04
 */
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

/**
 * 25. K 个一组翻转链表
 * 给你一个链表，每 k 个节点一组进行翻转，请你返回翻转后的链表。
 * k 是一个正整数，它的值小于或等于链表的长度。
 *
 * 如果节点总数不是 k 的整数倍，那么请将最后剩余的节点保持原有顺序。
 * 示例：
 * 给你这个链表：1->2->3->4->5
 *
 * 当 k = 2 时，应当返回: 2->1->4->3->5
 *
 * 当 k = 3 时，应当返回: 3->2->1->4->5
 * 说明：
 *
 * 你的算法只能使用常数的额外空间。
 * 你不能只是单纯的改变节点内部的值，而是需要实际进行节点交换。
 */
class Solution {

//     * 给你这个链表：1->2->3->4->5
//     * 当 k = 2 时，应当返回: 2->1->4->3->5
//    当 k = 3 时，应当返回: 3->2->1->4->5
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode cur = head;
        int n = 0; //翻转次数
        ListNode totalHead = null; //返回总链表的头部，
        ListNode tempTail = null; //总链表的临时尾部
        while (cur!=null){
            ListNode curHead = cur;
            int i=0;
            for (;i<k && cur!=null;i++){
                cur = cur.next;
            }
            if(i==k){ //需要翻转k个节点
                ListNode[] reverse = reverse(curHead, cur);
                ListNode newHead = reverse[0];
                ListNode newTail = reverse[1];
                n++;
                if(n==1){ //第一次翻转
                    totalHead = newHead; //总链表头结点为翻转链表的头
                    tempTail = newTail;//总链表尾结点为翻转链表的尾
                }else {//大于第一次翻转
                    tempTail.next = newHead; //总链表尾结点next指向翻转链表的头
                    tempTail = newTail;//总链表尾结点指向翻转链表的尾
                }
            }else { //最后的节点小于k个不需要翻转
                if(n==0){ //如果一次也没翻转，直接返回头节点
                    return  head;
                }else { //总链表的尾部next指向当前链表头部
                    tempTail.next = curHead;
                }
            }
        }
        return totalHead;
    }

    //1->2->3    1<-2,返回翻转后的头、尾节点，这里的3节点不翻转，是为了边界判断
    public ListNode[] reverse(ListNode head, ListNode tail) {
        ListNode pre = null; //翻转后的头结点
        ListNode next = null;
        ListNode cur = head;
        while (cur!=tail){
            next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return new ListNode[]{pre,head};
    }
    public void printList(ListNode head){
        while (head!=null){
            System.out.print( head.val + " ") ;
            head= head.next;
        }
        System.out.println();
    }
    public static void main(String[] args) {
        Solution solution = new Solution();
        ListNode head = new ListNode(1);
        ListNode cur = head;
        for (int i = 2; i < 6; i++) {
            ListNode node = new ListNode(i);
            cur.next = node;
            cur = node;
        }
        cur.next = null;
        solution.printList(head);
        ListNode listNode = solution.reverseKGroup(head, 2);
        solution.printList(listNode);
    }
}