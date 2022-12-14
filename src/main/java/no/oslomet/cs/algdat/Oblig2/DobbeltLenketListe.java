package no.oslomet.cs.algdat.Oblig2;


////////////////// class DobbeltLenketListe //////////////////////////////


import java.util.*;


public class DobbeltLenketListe<T> implements Liste<T> {

    /**
     * Node class
     *
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen

    public DobbeltLenketListe() {

        hode = hale = null; // hode og hale til null
        antall = 0;        // Ingen verdier i listen
        endringer = 0;   // Ingen endrigner i listen
    }

    public DobbeltLenketListe(T[] a) {  //Konstruktør

        if (a == null) {
            throw new NullPointerException();
        }

        if (a.length > 0) {
            int i = 0;
            for (; i < a.length; i++) { //finner første ikke null element og lager hode
                if (a[i] != null) {

                    hode = new Node<>(a[i]);
                    antall++;
                    break;
                }
            }
            hale = hode; //Siden det er bare en node så er hode og hale samme.
            if (hode != null) {     //Lager resten av listen
                i++;
                for (; i < a.length; i++) {
                    if (a[i] != null) {

                        hale.neste = new Node<>(a[i], hale, null);
                        hale = hale.neste;
                        antall++;
                    }
                }
            }
        }
    }
    private Node<T> finnNode(int indeks) {

        indeksKontroll(indeks, false);
        Node<T> p;
        //Sjekker fra hode hvis antallet delt på to er mindre enn indeks
        if (antall/2 > indeks) {

            p = hode;

            for (int i = 0; i < indeks; i++) {
                p = p.neste;

            }
            return p;
        }
        //Sjekker fra hale hvis det ikke er
        else {
            p = hale;
            for (int i = antall-1; i >indeks; i-- ) {
                p = p.forrige;

            }

            return p;
        }
    }


    // Bruker fratilKontroll() kontroll fra kompendiet
    private static void fratilKontroll(int antall, int fra, int til)
    {
        if (fra < 0)                                  // fra er negativ
            throw new IndexOutOfBoundsException
                    ("fra(" + fra + ") er negativ!");

        if (til > antall)                          // til er utenfor tabellen
            throw new IndexOutOfBoundsException
                    ("til(" + til + ") > antall(" + antall + ")");

        if (fra > til)                                // fra er større enn til
            throw new IllegalArgumentException
                    ("fra(" + fra + ") > til(" + til + ") - illegalt intervall!");
    }


    public Liste<T> subliste(int fra, int til) {

        fratilKontroll(antall,fra,til);

        DobbeltLenketListe<T> liste = new DobbeltLenketListe<>();


        int intervall = til - fra;

        if (antall < 1) return liste;

        Node<T> p = finnNode(fra);

        //Lager en while-løkke helt til at det går gjennom hele intervallet.
        while (intervall > 0) {
            liste.leggInn(p.verdi);
            p = p.neste;
            intervall--;
        }
        return liste;
    }

    @Override
    public int antall() {
        return antall;
    }

    @Override
    public boolean tom() {
     return antall ==0;
    }

    @Override
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi);

        //Sjekker om listen er tom
        if (tom()) { //Legger inn en  node
            hode = hale= new Node<>(verdi);
        }
        else  { //Legger inn noden bakerst i listen
            hale.neste = new Node<>(verdi, hale, null);
            hale = hale.neste;
        }

        endringer++;
        antall++;
        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi) {

        Objects.requireNonNull(verdi);

        indeksKontroll(indeks,true);
        if (antall == 0) { //Hvis det er tom liste
            hode = hale = new Node<>(verdi,null,null);
        }

        else if (indeks == 0) { //Legger inn verdien som hode
            hode.forrige = new Node<>(verdi,null,hode);
            hode = hode.forrige;
        }
        else if (indeks == antall) {  //Legger inn verdien bakerst
            hale.neste = new Node<>(verdi,hale, null);
            hale = hale.neste;
        }

        else { //Legger til verdien midt i lisen
            Node<T> p = finnNode(indeks-1);
            Node<T> n = p.neste;
            p.neste = new Node<>(verdi, p, p.neste); //Legger til verdi mellom p og p.neste
            n.forrige = p.neste;
        }
        antall++;
        endringer++;
    }

    @Override
    public boolean inneholder(T verdi) {

        if (indeksTil(verdi) != -1) return true;

        return false;
    }

    @Override
    public T hent(int indeks) {
        indeksKontroll(indeks,false);

        Node <T> p = finnNode(indeks);

        return p.verdi;

    }

    @Override
    public int indeksTil(T verdi) {

        if (verdi == null) return -1;

        Node<T> p = hode;

        for (int indeks = 0; indeks < antall; indeks++) {
            if (verdi.equals(p.verdi)) return indeks;
            p = p.neste;

        }
        return -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        Objects.requireNonNull(nyverdi, "Ikke tillatt med null-verdier!");
        indeksKontroll(indeks, false);

        Node <T> p = finnNode(indeks);
        T gammelverdi = p.verdi;

        p.verdi = nyverdi;
        endringer++;
        return gammelverdi;

    }

    @Override
    public boolean fjern(T verdi) {
        if (verdi == null) return false; // ingen nullverdier i listen

        Node <T> node = hode;

        while (node != null) {
            if (node.verdi.equals(verdi)) break; //Går gjennom løkken helt til vi finner riktig verdi
            node = node.neste;
        }

        if (node == null) return false; //returnerer false hvis den ikke finnes

        else if (antall ==1) {
            hode = hale = null; //Tommer ut listen hvis det er bare en node
        }
        else if (node == hode) {
            hode = hode.neste;
            hode.forrige = null;
        }
        else if (node == hale) { //Hvis verdien er i siste posisjon
            hale = hale.forrige;
            hale.neste = null;
        }
        else {
            node.forrige.neste = node.neste;
            node.neste.forrige = node.forrige;
        }

        antall--; //Fjerner en verdi
        endringer++;


        return true;
    }

    @Override
    public T fjern(int indeks) {
        indeksKontroll(indeks,false);

        Node<T> temp = hode;

        if (antall == 1) { //Hvis det er bare en node så peker de til samme verdi
            hode = hale = null;
        }

        else if (indeks == 0) { //Hvis indeksen er 0 så fjerner vi første verdi

            hode = hode.neste;
            hode.forrige = null;

        }
        else if (indeks == antall-1) { //Hvis det er siste node som skal fjernes
            hale = hale.forrige;
            hale.neste = null;
        }
        else { //Hvis det er node midt i tabellen som skal fjernes
            temp = finnNode(indeks);
            Node<T> q = temp.forrige;
            Node<T> p = temp.neste;

            temp.forrige.neste = p;
            temp.neste.forrige = q;

        }
        //Skriver ut verdien som skal fjernes
        T verdiFjern = temp.verdi;

        antall--; //Oppdaterer antall
        endringer++;

        return verdiFjern;
    }

    public void nullstill()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {

        if (antall == 0) return "[]";

        StringBuilder s = new StringBuilder();


        if (!tom()) {
            s.append("[");
            Node <T> p = hode;
            s.append(p.verdi);

            p = p.neste;

            while (p != null) {
                s.append(",").append(" ").append(p.verdi);
                p = p.neste;
            }
        }
        s.append("]");
        return s.toString();
    }

    public String omvendtString() {
        if (antall == 0) return "[]";

        StringBuilder s = new StringBuilder();

        if (!tom()) {
            s.append("[");
            Node <T> b = hale;
            s.append(b.verdi);
            b = b.forrige;

/*
            for (int i = 0; i < antall -1; i--) {
                if (b!= null) {
                    s.append(", ").append(b.verdi);
                    b = b.forrige;
              }
            }

 */
            //Mer effektivt enn for-løkke
            while (b != null) {
                s.append(", ").append(b.verdi);
                b = b.forrige;
            }

        }
        s.append("]");

        return s.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new DobbeltLenketListeIterator();
    }

    public Iterator<T> iterator(int indeks) {
        indeksKontroll(indeks,false);

       return new DobbeltLenketListeIterator(indeks);

    }


    private class DobbeltLenketListeIterator implements Iterator<T> {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator() {
            denne = hode;     // p starter på den første i listen
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks) {
            denne = finnNode(indeks);
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer

        }

        @Override
        public boolean hasNext() {
            return denne != null;
        }

        @Override
        public T next() {
            if (iteratorendringer != endringer) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            fjernOK = true;
            T verdi = denne.verdi;
            denne = denne.neste;
            return verdi;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {

    }

} // class DobbeltLenketListe


