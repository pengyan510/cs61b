public class RunList {

	private Run head;
	private int size;

	public RunList() {
		head = new Run();
		head.prev = head;
		head.next = head;
		head.red = -1;
		head.green = -1;
		head.blue = -1;
	}

	public Run getHead() {
		return head;
	}

	public int getSize() {
		return size;
	}

	public void insertEnd(int red, int green, int blue, int length) {
		Run node = new Run(red, green, blue, length);
		head.prev.next = node;
		node.prev = head.prev;
		node.next = head;
		head.prev = node;
		size++;
	}

	public void insertAfter(Run node, int red, int green, int blue, int length) {
		Run newNode = new Run(red, green, blue, length);
		node.next.prev = newNode;
		newNode.next = node.next;
		newNode.prev = node;
		node.next = newNode;
		size++;
	}

	public void delete(Run node) {
		node.prev.next = node.next;
		node.next.prev = node.prev;
		size--;
	}

	public void deleteOneLength(Run node) {
		node.length--;
		if (node.length == 0) {
			delete(node);
		}
	}

	public static void main(String[] args) {
		RunList rList = new RunList();
		int a = 1, b = 2, c = 3, d = 4;
		rList.insertEnd(a, b, c, d);
		rList.insertEnd(d, c, b, a);
		Run head = rList.getHead().next;
		int size = rList.getSize();
		while (size > 0) {
			System.out.println(head.red);
			head = head.next;
			size--;
		}
	}
}