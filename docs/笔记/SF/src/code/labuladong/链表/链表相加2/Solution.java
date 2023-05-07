package code.labuladong.链表.链表相加2;

import code.labuladong.链表.ListNode;

import java.util.*;

/*
 * public class ListNode {
 *   int val;
 *   ListNode next = null;
 * }
 */

public class Solution {

    public static void main(String[] args) {
//        [9,3,7],[6,3]
        ListNode listNode1 = new ListNode(9).setNext(new ListNode(3).setNext(new ListNode(7)));
        ListNode listNode2 = new ListNode(6).setNext(new ListNode(3));

        System.out.println(new Solution().addInList(listNode1,listNode2));
    }
    /**
     * 
     * @param head1 ListNode类 
     * @param head2 ListNode类 
     * @return ListNode类
     */
    public ListNode reverse (ListNode head){
        ListNode pre = null;
        ListNode cur = head;
// 1->2->3
        while(cur!=null){
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;

    }

    public ListNode addInList (ListNode head1, ListNode head2) {
        // write code here
        if(head1 == null){
            return head2;
        }
        if(head2 == null){
            return head1;
        }
        Stack<Integer> l1 = new Stack();
        Stack<Integer> l2 = new Stack();
        List<Integer> list = new ArrayList();
        head1 = reverse(head1);
        head2 = reverse(head2);
        ListNode p1 = head1;
        ListNode p2 = head2;
        int jinwei = 0;
        while(p1 != null || p2 != null){
            if(p1 == null && p2 == null){
                break;
            }
            int a = 0;
            int b = 0;
            if(p1 != null){
                a = (p1.val);
                p1 = p1.next;
            }
            if(p2 != null){
                b = (p2.val);
                p2 = p2.next;
            }
            int sum = a + b + jinwei;
            if(sum >9){
                list.add(sum-10);
                jinwei = 1;
            }else{
                list.add(sum);
                jinwei = 0;
            }

        }
        if(jinwei == 1){
            list.add(1);
        }
        ListNode dummy = new ListNode(-1);
        ListNode p = dummy;
        for(int i=list.size()-1;i>=0;i--){
            p.next = new ListNode(list.get(i));
            p = p.next;
        }
        return dummy.next;
    }
}