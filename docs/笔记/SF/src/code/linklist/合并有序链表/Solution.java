package code.linklist.合并有序链表;

import java.util.*;

 class ListNode {
   int val;
   ListNode next = null;

     public ListNode(int val) {
         this.val = val;
     }
 }

public class Solution {
    /**
     * 
     * @param l1 ListNode类 
     * @param l2 ListNode类 
     * @return ListNode类
     */
//     public ListNode mergeTwoLists (ListNode l1, ListNode l2) {
//         // write code here
//         if(l1==null && l2==null){
//             return null;
//         }
//         List<Integer> list = new ArrayList<Integer>();
//         while(l1!=null){
//             list.add(l1.val);
//             l1=l1.next;
//         }
//         while(l2!=null){
//             list.add(l2.val);
//             l2=l2.next;
//         }
//         Collections.sort(list);
//         ListNode head = new ListNode(list.get(0));
//         ListNode cur = head;
//         for(int i =1;i<list.size();i++){
//             ListNode node = new ListNode(list.get(i));
//             cur.next = node;
//             cur = cur.next;
//         }
//         cur.next = null;
//         return head;
//     }
    
    public ListNode mergeTwoLists (ListNode l1, ListNode l2) {
        // write code here
        if(l1==null && l2==null){
            return null;
        }
        if(l1==null && l2!=null){
            return l2;
        }
        if(l2==null && l1!=null){
            return l1;
        }
        
        ListNode head = null;
        if(l1.val>l2.val){
            head = l2;
            l2 = l2.next;
        }else{
            head = l1;
            l1=l1.next;
        }
        ListNode cur = head;
        while(l1!=null && l2!=null){
            if(l1.val>l2.val){
                cur.next = l2;
                cur = l2;
                l2 = l2.next;
            }else{
                cur.next = l1;
                cur = l1;
                l1=l1.next;
            }
        }
        if(l1==null && l2!=null){
            cur.next = l2;
        }else if(l2==null && l1!=null){
            cur.next = l1;
        }else if(l2==null && l1==null){
            cur.next = null;
        }
        return head;
    }

    public static void main(String[] args) {
        ListNode l1 = new ListNode(2);
        ListNode l2 = new ListNode(1);

        ListNode listNode = new Solution().mergeTwoLists(l1, l2);
        System.out.println(listNode);
    }
}