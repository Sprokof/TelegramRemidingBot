package telegramBot.myCollectionFramework;

//Add selfVersion of Map just for test how it works
public class MyMap<K, V> {
    private  MyMap.MyEntry<K,V>[] entryArray;
    private int size;

    public MyMap(){
        this.entryArray = new MyMap.MyEntry[100];
        this.size = entryArray.length;
        }

        public int size(){
            return size;}

        private void grow(){
            MyMap.MyEntry<K,V>[] temp = new MyMap.MyEntry[entryArray.length*2];
            System.arraycopy(entryArray, 0 ,temp, 0, entryArray.length);
            entryArray = temp;}


        public boolean put(K key, V value) {
            int index = (new MyMap.MyEntry<K,V>(key, value).hashCode())%entryArray.length;
            if(entryArray[index]==null){entryArray[index] = new MyMap.MyEntry<K, V>(key,value);
                if(index==entryArray.length) grow();
                return true;}
            else if(entryArray[index]!=null&&!entryArray[index].equals(new MyEntry<>(key,value))){
                int in = 0;
                while (true){
                    in++;
                    if(entryArray[in]==null){entryArray[in] = new MyMap.MyEntry<K,V>(key, value); break;}}
                if(in==entryArray.length){grow();}
                return true;}
            return false;}


        public V getValue(K key){
            int index = new MyMap.MyEntry<K,V>(key).hashCode()%entryArray.length;
            if(entryArray[index]!=null){return entryArray[index].getValue();}
            return null;
        }

        public K getKey(V value){
            for(int i = 0; i < entryArray.length; i++){
                if(entryArray[i]==null){continue;}
                if(entryArray[i].getValue().equals(value)){
                    return entryArray[i].getKey();}}
            return null;}

        public boolean remove(K key) {
            int index = new MyMap.MyEntry<K,V>(key).hashCode()%entryArray.length;
            if (entryArray[index].getKey().equals(key)) {
                entryArray[index] = null;
                size--;
                condenseArray(index);
                return true;}
            return false;}



        private void condenseArray(int start) {
            for (int i = start; i < size; i++) {
                entryArray[i] = entryArray[i + 1];
            }
        }

        private static class MyEntry<K,V>{
            private K key;
            private V value;

            private K getKey(){
                return key;}

            private V getValue(){
                return value;}

            private MyEntry(K key, V value){
                this.key = key;
                this.value = value;}

            private MyEntry(K key){
                this.key = key;}

            @Override
            public int hashCode(){
                if(this.getKey() instanceof Integer){
                    return 31*(int)getKey();}
                else{
                    String temp = getKey().toString();
                    char[] chArray = temp.toCharArray();
                    int result = (int)Character.toUpperCase(chArray[0]);
                    for(int i = 1; i<chArray.length; i++){
                        result+=(int)chArray[i];}
                    return result*31;}}

            @Override
            public boolean equals(Object obj){
                if(this==obj){return true;}
                if(obj==null){return false;}
                if(!(obj instanceof MyEntry)){return false;}
                MyEntry<K,V> main = (MyEntry) obj;
                return this.key==main.key;}

        }
    }

