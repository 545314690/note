package code.labuladong.链表.lcode92翻转链表2;

import code.labuladong.链表.ListNode;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

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
 * 给你单链表的头指针 head 和两个整数 left 和 right ，其中 left <= right 。请你反转从位置 left 到位置 right 的链表节点，返回 反转后的链表 。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/reverse-linked-list-ii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 *
 * 输入：head = [1,2,3,4,5], left = 2, right = 4
 * 输出：[1,4,3,2,5]
 * 示例 2：
 *
 * 输入：head = [5], left = 1, right = 1
 * 输出：[5]
 *
 */
public class Solution {

    public static void main(String[] args) {
        ListNode list1 = new ListNode(1);
        list1.setNext(new ListNode(2)
        .setNext(new ListNode(3).setNext(new ListNode(4).setNext(new ListNode(5))))
        );



        ListNode listNode = new Solution().reverseBetween3(list1,2,4);
        System.out.println(listNode);
    }

    public ListNode reverseBetween3 (ListNode head, int m, int n) {
        //判断null和m=n的情况，无需反转
        if(head == null || m==n){
            return head;
        }
        ListNode res = new ListNode(-1);
        res.next = head;
        ListNode pre = res;
        ListNode cur = head;

        for(int i=1;i<m;i++){
            pre = pre.next;
            cur = cur.next;
        }
        for(int i=1;i< n-m+1;i++){
//            ListNode temp = cur.next;
//            cur.next = pre;
//            pre = cur;
//            cur = temp;
            ListNode temp = cur.next;
            cur.next = temp.next;
            temp.next = pre.next;
            pre.next = temp;
        }
        return res.next;
    }
    public ListNode reverseBetween2 (ListNode head, int m, int n) {
        //判断null和m=n的情况，无需反转
        if(head == null || m==n){
            return head;
        }
        //保存新的头节点
        ListNode newHead = head;
        ListNode p = head;
        //记录第1段的结尾
        ListNode tail1 = null;
        //
        int i = 1;
        while(i < m){
            tail1 = p;
            p = p.next;
            i++;
        }
        //第1段结束，此时如果tail1为null，则代表m=1，新的链表头节点应该为第2段的头节点

        //反转前第2段的头
        ListNode head2 = p;
        while(i < n){
            p = p.next;
            i++;
        }
        //第2段结束，此时p为第2段反转前的尾
        //第3段头
        ListNode head3 = p.next;
        //将第2段反转前的尾截断
        p.next = null;

        //开始反转第2段
        ListNode pre = null;
        ListNode cur = head2;
        ListNode next = null;
        while(cur != null){
            next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        //反转第2段结束，pre为第2段新头节点
        //如果第一段尾节点为空，整个链表的头为第2段新头节点，即pre
        if(tail1 == null){
            newHead = pre;
        }else {
            //否则第一段的尾接上第二段的头
            tail1.next = pre;
        }

        //第二段的尾巴（反转前的头）接上第三段的头，head3可能为null（此时n=链表长度）
        head2.next = head3;
        return newHead;

    }
    public ListNode reverseBetween(ListNode head, int left, int right) {
        if(left == right){
            return head;
        }

        if(head == null || head.next == null){
            return head;
        }
        Deque<ListNode> queue = new LinkedList<>();
        ListNode newHead = new ListNode();
        ListNode p = newHead;
        int i= 1;
        while (head != null){
            ListNode node = head;
            head = head.next;
            node.next = null;
            if(i<left){
                p.next = node;
                p = p.next;
            }else if(i>right){
                queue.addFirst(node);
            }else {
                queue.addLast(node);
            }
            i++;
        }

        while (!queue.isEmpty()){
            p.next = queue.pollLast();
            p = p.next;
        }
        return newHead.next;
    }
}