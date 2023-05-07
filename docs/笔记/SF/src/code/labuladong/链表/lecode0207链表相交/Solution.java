package code.labuladong.链表.lecode0207链表相交;

import code.labuladong.链表.ListNode;

/**
 * Definition for singly-linked list.
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */

/**
 * 给你两个单链表的头节点 headA 和 headB ，请你找出并返回两个单链表相交的起始节点。如果两个链表没有交点，返回 null 。
 *
 * https://leetcode-cn.com/problems/intersection-of-two-linked-lists-lcci/
 */
public class Solution {

    public static void main(String[] args) {
        ListNode list1 = new ListNode(1);
        list1.setNext(new ListNode(2).setNext(new ListNode(4)));

        ListNode list2 = new ListNode(1);

        list2.setNext(new ListNode(1).setNext(new ListNode(3).setNext(list1.next)));


        ListNode listNode = new Solution().getIntersectionNode(list1,list2);
        System.out.println(listNode.val);
    }

    /**
     * https://labuladong.gitee.io/algo/2/17/16/
     * @param headA ,headB
     * @return
     */

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if(headA == null || headB == null){
            return null;
        }
        ListNode p1 = headA;
        ListNode p2 = headB;
        while (p1 != p2){
            if(p1 == null){
                p1 = headB;
            }else {
                p1 = p1.next;
            }
            if(p2 == null){
                p2 = headA;
            }else {
                p2 = p2.next;
            }
        }
        return p1;
    }
}