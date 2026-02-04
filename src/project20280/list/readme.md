Q6. Singly Linked List is a collection of nodes that together form a linear ordering. Each node stores a reference to an element and a reference to another node.
Circularly Linked List is the same as Singly Linked List, except it doesn’t have a pointer to null at the end, instead it points back to the head.

Q7. Linked lists are preferable over arrays when:
1.	You need constant-time insertions/deletions from the list
2.	You don't know how many items will be in the list
3.	You don't need random access to any elements
4.	You want to be able to insert items in the middle of the list

Q8. 
1.	Multiplayer board game - players are stored in a circularly linked list so turns advance endlessly in a fixed order. When the last player finishes a turn, the list naturally cycles back to the first player.
2.	Music playlist – a circularly linked list could represent songs to loop indefinitely. The structure allows seamless wraparound when the end is reached, avoiding resets.
