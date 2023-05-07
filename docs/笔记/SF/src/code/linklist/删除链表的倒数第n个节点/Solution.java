package code.linklist.删除链表的倒数第n个节点;

/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
//public class Solution {
//    public ListNode removeNthFromEnd(ListNode head, int n) {
//        int length = 0;
//        ListNode cur = head;
//        while(cur!=null){
//            length++;
//            cur = cur.next;
//        }
//        cur = head;
//        ListNode pre = null;
//        while(cur!=null){
//            pre = cur;
//            cur = cur.next;
//            if(length==n){
//                if(cur!=null){
//                    pre.next = cur.next;
//                }else{
//                    pre.next=null;
//                }
//            }
//            length--;
//        }
//        return head;
//
//    }
//}