package code.labuladong.链表.lcode206反转链表;

import code.labuladong.链表.ListNode;

import java.util.Stack;

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
 * 给给你单链表的头节点 head ，请你反转链表，并返回反转后的链表。
 * 输入：head = [1,2,3,4,5]
 * 输出：[5,4,3,2,1]
 *
 * https://leetcode-cn.com/problems/reverse-linked-list/
 */
class Solution {

    public static void main(String[] args) {
        ListNode list1 = new ListNode(1);
        list1.setNext(new ListNode(2).setNext(new ListNode(4)));


        ListNode listNode = new  Solution().ReverseList(list1);
        System.out.println(listNode);
    }

    public ListNode ReverseList(ListNode head) {
        if(head == null || head.next == null){
            return head;
        }

//         ListNode newHead = ReverseList(head.next);
//         head.next.next = head;
//         head.next = null;
//         return newHead;

        ListNode cur = head;
        ListNode pre = null;
        ListNode next = null;
        while(cur !=null){

            next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }
    /**
     * 利用栈先进后出的模式
     * @param head
     * @return
     */
    public ListNode reverseList2(ListNode head) {
        if(head == null || head.next == null){
            return head;
        }
        Stack<ListNode> stack = new Stack<>();

        while (head != null){
            ListNode p = head;
            head = head.next;
            p.next = null;
            stack.push(p);
        }
        ListNode newHead = new ListNode();
        ListNode p = newHead;
        while (!stack.empty()){
            p.next = stack.pop();
            p = p.next;
        }
        return newHead.next;
    }


    public ListNode reverseList(ListNode head) {
        if(head == null || head.next == null){
            return head;
        }
        ListNode tail =  reverseList(head.next);
        head.next.next = head;
        head.next = null;
        return tail;
    }

}