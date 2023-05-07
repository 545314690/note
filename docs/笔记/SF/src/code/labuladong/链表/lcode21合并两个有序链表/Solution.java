package code.labuladong.链表.lcode21合并两个有序链表;

import code.labuladong.链表.ListNode;

/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */


/**
 * https://leetcode-cn.com/problems/merge-two-sorted-lists/
 * 将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。
 *
 * 输入：l1 = [1,2,4], l2 = [1,3,4]
 * 输出：[1,1,2,3,4,4]
 */
class Solution {
    public static void main(String[] args) {
        ListNode list1 = new ListNode(1);
        list1.setNext(new ListNode(2).setNext(new ListNode(4)));

        ListNode list2 = new ListNode(1);
        list2.setNext(new ListNode(3).setNext(new ListNode(4)));


        ListNode listNode = new Solution().mergeTwoLists(list1, list2);
        System.out.println(listNode);
    }

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {

        //判断是否为空
        if(list1 == null && list2 == null){
            return null;
        }
        if(list1 != null && list2 == null){
            return list1;
        }
        if(list2 != null && list1 == null){
            return list2;
        }
        ListNode curr = new ListNode();
        ListNode head = curr;
        while (list1 != null && list2 != null){

            if(list1.val <= list2.val){
                curr.next = list1;
                list1 = list1.next;
            }else {
                curr.next = list2;
                list2 = list2.next;
            }
            curr = curr.next;
        }
        if(list1 != null){
            curr.next = list1;
        }
        if(list2 != null){
            curr.next = list2;
        }
        return head.next;
    }
}