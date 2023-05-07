package code.labuladong.链表.lcode23合并K个升序链表;

import code.labuladong.链表.ListNode;

import java.util.*;
import java.util.stream.Collectors;

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
 * 给你一个链表数组，每个链表都已经按升序排列。
 *
 * 请你将所有链表合并到一个升序链表中，返回合并后的链表。
 *
 *输入：lists = [[1,4,5],[1,3,4],[2,6]]
 * 输出：[1,1,2,3,4,4,5,6]
 * 解释：链表数组如下：
 * [
 *   1->4->5,
 *   1->3->4,
 *   2->6
 * ]
 * 将它们合并到一个有序链表中得到。
 * 1->1->2->3->4->4->5->6
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/merge-k-sorted-lists
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
class Solution {

    public static void main(String[] args) {
        ListNode list1 = new ListNode(1);
        list1.setNext(new ListNode(2).setNext(new ListNode(4)));

        ListNode list2 = new ListNode(1);
        list2.setNext(new ListNode(3).setNext(new ListNode(4)));
        ListNode list3 = new ListNode(1);
        list3.setNext(new ListNode(5).setNext(new ListNode(6)));


        ListNode listNode = new Solution().mergeKLists(new ListNode[]{list1, list2, list3});
        System.out.println(listNode);
    }

    public ListNode mergeKLists(ListNode[] lists) {

        if(lists.length == 0){
            return null;
        }
        ListNode head = new ListNode();
        ListNode curr = head;
        PriorityQueue<ListNode> queue = new PriorityQueue<>(lists.length, new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return o1.val - o2.val;
            }
        });
        for (ListNode h: lists){
            if(h != null){
                queue.add(h);
            }
        }

        while (!queue.isEmpty()){
            ListNode node = queue.poll();
            curr.next = node;
            node = node.next;
            curr = curr.next;
            if(node != null) {
                queue.add(node);
            }
        }
        return head.next;
    }
    /**
     * 暴力解法，每次获取最小结点
     * @param lists
     * @return
     */
    public ListNode mergeKLists2(ListNode[] lists) {

        ListNode head = new ListNode();
        ListNode curr = head;
        List<ListNode> notEmptyList = new ArrayList<>();
        while (true){
            notEmptyList = Arrays.stream(lists).filter(listNode -> Objects.nonNull(listNode)).collect(Collectors.toList());

            if(notEmptyList.size() <=1){
                break;
            }
            ListNode node = notEmptyList.stream().min(new Comparator<ListNode>() {
                @Override
                public int compare(ListNode o1, ListNode o2) {
                    if(o1 == null) return 1;
                    if(o2 == null) return -1;
                    return o1.val - o2.val;
                }
            }).get();
            curr.next = node;
            //更新下
            lists[Arrays.asList(lists).indexOf(node)] = node.next;
            curr = curr.next;
        }

        if(notEmptyList.size() >0){
            curr.next = notEmptyList.get(0);
        }
        return head.next;
    }
}