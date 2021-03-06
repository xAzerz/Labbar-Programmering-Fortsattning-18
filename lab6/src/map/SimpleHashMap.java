package map;

public class SimpleHashMap<K, V> implements Map<K, V>{
    private Entry<K, V>[] entries;
    private float loadFactor;
    private int capacity;
    private int size;
    private int tableSize;

    public SimpleHashMap() {
        size = 0;
        tableSize = 0;
        loadFactor = .75f;
        capacity = 16;
        entries = (Entry<K, V>[]) new Entry[capacity];
    }
    public SimpleHashMap(int capacity) {
        size = 0;
        tableSize = 0;
        this.capacity = capacity;
        loadFactor = .75f;
        entries = (Entry<K, V>[]) new Entry[capacity];
    }

    @Override
    public V get(Object key) {
        //TODO: typkonvertera till K
        K keyK = (K) key;
        //eller gör jobbiga sättet, vilket är dumt

        Entry<K, V> entry = find(keyK);
        if(entry != null) {
            return entry.getValue();
        }else {
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        if(size == 0) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public  V put(K key, V value) {
        return put(key, value, entries);
    }

    public V put(K key, V value, Entry<K, V>[] localEntries) {
        //kolla om den överstiger loadfactor -> rehash
        if(false/*loadFactor <= tableSize/capacity*/){
            rehash();
        }else {
            Entry<K, V> newEntry = new Entry<K, V>(key, value);

            int index = index(key);
            //kolla om vektorplats tom eller ej
            if(localEntries[index] == null) {
                localEntries[index] = newEntry;  //lägga in
                tableSize++;
                size++;
            }else if(localEntries[index].equals(value)){
                return null;
            } else {
                //lägg in ny entry i if no entry is same as sista entry (entry.next == null)
                if(find(key) == null){
                    Entry<K, V> oldFirstEntry = localEntries[index];
                    localEntries[index] = newEntry;
                    localEntries[index].next = oldFirstEntry;
                    size++;
                }else{
                    return null;
                }
            }
        }
        return value;
    }

    @Override
    public V remove(Object key) {
        K keyK = (K) key;

        //FALL1: lista null
        //FALL2: key i första element
        //FALL3: key senare i listan
        //fall4: key finns ej

        int index = index(keyK);

        //Handle if map is empty or place is null
        if(isEmpty() || entries[index] == null){
            return null;
        }else{
        //if there's entries at table[index]
            if(entries[index].key.equals(keyK)){      //if first in linkedlist
                Entry<K, V> old = entries[index];

                entries[index] = entries[index].next;
                size--;
                if(old.next == null){            //if no elements left in vectorspace
                    tableSize--;
                }
                return old.value;
            }else if(find(keyK)==null){         //if key is not find - cant remove
                return null;
            }else{
                //loopa igenom
                Entry<K, V> old = null;
                Entry<K, V> current = entries[index];
                while(current != null) {
                    if(current.key.equals(keyK)) {
                        //ta bort
                        old.next = current.next;
                        size--;
                    }
                    old = current;
                    current = current.next;
                }
                return old.value;
            }
        }
    }

    private void rehash() {
        //dubblera tabellen (ändra capacity och ny tabell)
        capacity = capacity*2;
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[capacity];

        //loopa igenom gamla tabellen
        for(Entry<K, V> entry: entries) {

            Entry<K, V> currentEntry = entry;

            //loop igenom alla länkade entries
            while(entry != null) {
                //put entry from old vector in new vector
                put(entry.key, entry.value, newTable);
                //scope to new entry thats linked
                currentEntry = currentEntry.next;
            }
        }

        entries = newTable;
    }

    /**
     *
     * FIND
     *
     * */
    private Entry<K, V> find(K key) {
        int index = index(key);

        //basentryn
        Entry<K, V> currentEntry = entries[index];

        while(currentEntry != null){
            //cond for find key - no risk for nullpointer
            if(currentEntry.key.equals(key)){
                return currentEntry;
            }
            currentEntry = currentEntry.next;
        }
        return null;
    }

    private int index(K key) {
        int index;

        //fixa hashcoden returnera var den borde ha för index
        index = Math.abs(key.hashCode() % capacity);
        return index;
    }

    @Override
    public String show(){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < capacity; i++){
            Entry<K, V> currentEntry = entries[i];  //keeps track of current entry of all that collides

            //loopa igenom alla next
            sb.append(i + "         ");
            while(currentEntry != null) {
                sb.append(currentEntry.toString() + " ");
                currentEntry = currentEntry.next;
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    public static class Entry<K, V> implements Map.Entry<K, V>{
        private K key;
        private V value;
        public Entry<K, V> next;

        public Entry(K key, V value){
            this.key = key;
            this.value = value;
            next = null;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }
    }
}
