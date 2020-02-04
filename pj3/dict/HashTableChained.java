/* HashTableChained.java */

package dict;

import list.*;

/**
 *  HashTableChained implements a Dictionary as a hash table with chaining.
 *  All objects used as keys must have a valid hashCode() method, which is
 *  used to determine which bucket of the hash table an entry is stored in.
 *  Each object's hashCode() is presumed to return an int between
 *  Integer.MIN_VALUE and Integer.MAX_VALUE.  The HashTableChained class
 *  implements only the compression function, which maps the hash code to
 *  a bucket in the table's range.
 *
 *  DO NOT CHANGE ANY PROTOTYPES IN THIS FILE.
 **/

public class HashTableChained implements Dictionary {

  /**
   *  Place any data fields here.
   **/
  DList[] hash;
  int size, collisions;
  final static int primeNum = 97;

  /** 
   *  Construct a new empty hash table intended to hold roughly sizeEstimate
   *  entries.  (The precise number of buckets is up to you, but we recommend
   *  you use a prime number, and shoot for a load factor between 0.5 and 1.)
   **/

  public HashTableChained(int sizeEstimate) {
    int num = sizeEstimate * 2 - 1;
    while (num > 1) {
      boolean flag = true;
      for (int i = 2; i <= Math.sqrt(num); i++) {
        if (num % i == 0) {
          flag = false;
          break;
        }
      }
      if (flag) {
        break;
      }
      num--;
    }


    hash = new DList[num];
    for (int i = 0; i < num; i++) {
    	hash[i] = new DList();
    }
  }

  /** 
   *  Construct a new empty hash table with a default size.  Say, a prime in
   *  the neighborhood of 100.
   **/

  public HashTableChained() {
    hash = new DList[primeNum];
    for (int i = 0; i < primeNum; i++) {
    	hash[i] = new DList();
    }
  }

  /**
   *  Converts a hash code in the range Integer.MIN_VALUE...Integer.MAX_VALUE
   *  to a value in the range 0...(size of hash table) - 1.
   *
   *  This function should have package protection (so we can test it), and
   *  should be used by insert, find, and remove.
   **/

  int compFunction(int code) {
    int pos = code % hash.length;
    if (pos < 0) {
      pos += hash.length;
    }
    return pos;
  }

  /** 
   *  Returns the number of entries stored in the dictionary.  Entries with
   *  the same key (or even the same key and value) each still count as
   *  a separate entry.
   *  @return number of entries in the dictionary.
   **/

  public int size() {
    return size;
  }

  public int collisions() {
    return collisions;
  }

  /** 
   *  Tests if the dictionary is empty.
   *
   *  @return true if the dictionary has no entries; false otherwise.
   **/

  public boolean isEmpty() {
    return size == 0;
  }

  private void resize(int newSize) {
    DList[] temp = hash;
    hash = new DList[newSize];
    for (int i = 0; i < newSize; i++) {
      hash[i] = new DList();
    }
    for (int i = 0; i < temp.length; i++) {
      while (! temp[i].isEmpty()) {
        try {
          ListNode node = temp[i].front();
          insert(((Entry) (node.item())).key(), ((Entry) (node.item())).value());
          node.remove();
        }
        catch (InvalidNodeException e) {
          System.err.println(e);
        }
      }
    }
  }

  /**
   *  Create a new Entry object referencing the input key and associated value,
   *  and insert the entry into the dictionary.  Return a reference to the new
   *  entry.  Multiple entries with the same key (or even the same key and
   *  value) can coexist in the dictionary.
   *
   *  This method should run in O(1) time if the number of collisions is small.
   *
   *  @param key the key by which the entry can be retrieved.
   *  @param value an arbitrary object.
   *  @return an entry containing the key and value.
   **/

  public Entry insert(Object key, Object value) {
    if (size == hash.length) {
      resize(2 * size);
    }
    Entry e = new Entry();
    e.key = key;
    e.value = value;
    int pos = compFunction(key.hashCode());
    if (!hash[pos].isEmpty()) {
      collisions++;
    }
    hash[pos].insertBack(e);
    size++;
    return e;
  }

  /** 
   *  Search for an entry with the specified key.  If such an entry is found,
   *  return it; otherwise return null.  If several entries have the specified
   *  key, choose one arbitrarily and return it.
   *
   *  This method should run in O(1) time if the number of collisions is small.
   *
   *  @param key the search key.
   *  @return an entry containing the key and an associated value, or null if
   *          no entry contains the specified key.
   **/

  public Entry find(Object key) {
    int pos = compFunction(key.hashCode());
    ListNode node = hash[pos].front();
    while (node.isValidNode()) {
      try {
      	if (((Entry) node.item()).key().equals(key)) {
        	return (Entry) node.item();
      	}
      	node = node.next();
      } catch (InvalidNodeException e) {
      	System.err.println(e);
      }
    }
    return null;
  }

  /** 
   *  Remove an entry with the specified key.  If such an entry is found,
   *  remove it from the table and return it; otherwise return null.
   *  If several entries have the specified key, choose one arbitrarily, then
   *  remove and return it.
   *
   *  This method should run in O(1) time if the number of collisions is small.
   *
   *  @param key the search key.
   *  @return an entry containing the key and an associated value, or null if
   *          no entry contains the specified key.
   */

  public Entry remove(Object key) {
    if (size <= hash.length / 4) {
      resize(size * 2);
    }
    int pos = compFunction(key.hashCode());
    ListNode node = hash[pos].front();
    while (node.isValidNode()) {
      try {
      	if (((Entry) node.item()).key().equals(key)) {
        	Entry temp = (Entry) node.item();
          node.remove();
        	size--;
        	return temp;
      	}
      	node = node.next();
      } catch (InvalidNodeException e) {
      	System.err.println(e);
      }
    }
    return null;
  }

  public void distribution() {
    for (int i = 0; i < hash.length; i++) {
      System.out.print(hash[i].length() + "  ");
    }
    System.out.println();
  }

  /**
   *  Remove all entries from the dictionary.
   */
  public void makeEmpty() {
    collisions = 0;
    for (int pos = 0; pos < hash.length; pos++) {
      if (hash[pos].isEmpty()) {
        continue;
      }
      ListNode node = hash[pos].front();
      while (node.isValidNode()) {
        ListNode temp = node;
        try {
        	node = node.next();
        	temp.remove();
        } catch (InvalidNodeException e) {
        	System.err.println(e);
        }
        size--;
      }
    }
  }

}
