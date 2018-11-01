package algdatoblig3;

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T>
{
    
    private static final class Node<T>   // en indre nodeklasse
    {
        private T verdi;                   // nodens verdi
        private Node<T> venstre, høyre;    // venstre og høyre barn
        private Node<T> forelder;          // forelder
        
        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder){
            this.verdi = verdi;
            venstre = v; 
            høyre = h;
            this.forelder = forelder;
        }

        private Node(T verdi, Node<T> forelder)  // konstruktør
        {
            this(verdi, null, null, forelder);
        }

        @Override
        public String toString(){ 
            return "" + verdi;
        }
    } // class Node

    private Node<T> rot;                            // peker til rotnoden
    private int antall;                             // antall noder
    private int endringer;                          // antall endringer
    
    private final Comparator<? super T> comp;       // komparator

    public ObligSBinTre(Comparator<? super T> c)    // konstruktør
    {
        rot = null;
        antall = 0;
        comp = c;
    }

    @Override
    public final boolean leggInn(T verdi)    // skal ligge i class SBinTre
    {
        Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

        Node<T> p = rot, q = null;               // p starter i roten
        int cmp = 0;                             // hjelpevariabel

        while (p != null)       // fortsetter til p er ute av treet
        {
            q = p;                                 // q er forelder til p
            cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
            p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
        }

        // p er nå null, dvs. ute av treet, q er den siste vi passerte

        p = new Node<T>(verdi, q);            // oppretter en ny node

        if (q == null) rot = p;                  // p blir rotnode
        else if (cmp < 0) q.venstre = p;         // venstre barn til q
        else q.høyre = p;                        // høyre barn til q

        antall++;                                // én verdi mer i treet
        return true;                             // vellykket innlegging
    }

    @Override
    public boolean inneholder(T verdi)
    {
        if(verdi == null)  
            return false;

        Node<T> p = rot;

        while(p != null)
        {
            int cmp = comp.compare(verdi, p.verdi);

            if(cmp < 0) 
                p = p.venstre;
            else if(cmp > 0) 
                p = p.høyre;
            else 
                return true;
        }
        return false;
    }

    @Override
    public boolean fjern(T verdi)  // hører til klassen SBinTre
    {
        if (verdi == null) return false;  // treet har ingen nullverdier

        Node<T> p = rot, q = null;   // q skal være forelder til p

        while (p != null)            // leter etter verdi
        {
            int cmp = comp.compare(verdi,p.verdi);      // sammenligner
            if (cmp < 0) { q = p; p = p.venstre; }      // går til venstre
            else if (cmp > 0) { q = p; p = p.høyre; }   // går til høyre
            else break;    // den søkte verdien ligger i p
        }
        if (p == null) return false;   // finner ikke verdi

        if (p.venstre == null || p.høyre == null)  // Tilfelle 1) og 2)
        {
            Node<T> b = p.venstre != null ? p.venstre : p.høyre;  // b for barn
            if (p == rot){
                rot = b;
            }else if (p == q.venstre){
                q.venstre = b;
            }else{
                q.høyre = b;
            }
            
            if(b != null){
                b.forelder = q;
            }
        }
        else  // Tilfelle 3)
        {
            Node<T> s = p, r = p.høyre;   // finner neste i inorden
            while (r.venstre != null)
            {
                s = r;    // s er forelder til r
                r = r.venstre;
            }

            p.verdi = r.verdi;   // kopierer verdien i r til p

            if (s != p){
                s.venstre = r.høyre;
                if(r.høyre != null){
                    r.høyre.forelder = s;
                }
            }else{
                s.høyre = r.høyre;
                if(r.høyre != null){
                    r.høyre.forelder = s;
                }
            }
        }

        antall--;   // det er nå én node mindre i treet
        return true;
  }

    public int fjernAlle(T verdi)
    {
        int ant = 0; 
        while(inneholder(verdi)){
            fjern(verdi);
            ant++;
        }
        return ant;
    }

    @Override
    public int antall()
    {
        return antall;
    }

    public int antall(T verdi)
    {
        if(verdi == null) return 0;
        
        Node<T> p = rot;
        int ant = 0;
        
        while(p != null){
            if(p.verdi.equals(verdi)){
                ant++;
            }
            
            int cmp = comp.compare(verdi, p.verdi);
            
            if(cmp < 0) 
                p = p.venstre;
            else if(cmp >= 0) 
                p = p.høyre;
        }
        
        return ant;
    }

    @Override
    public boolean tom()
    {
        return antall == 0;
    }

    @Override
    public void nullstill()
    {
        Node<T> plass = rot;
        
        while(antall > 0){
            if(plass.venstre != null){
                while(plass.venstre != null){
                    plass = plass.venstre;
                }
                if(plass.høyre == null && plass.venstre == null){
                    plass.verdi = null;
                    plass.forelder.venstre = null;
                    antall--;
                    plass = rot;
                }
            }else if(plass.høyre != null){
                while(plass.høyre != null){
                    plass = plass.høyre;
                }
                if(plass.høyre == null && plass.venstre == null){
                    plass.verdi = null;
                    plass.forelder.høyre = null;
                    antall--;
                    plass = rot;
                }
            }else if(plass.høyre == null && plass.venstre == null && plass == rot){
                rot = null;
                antall--;
            }
        }
    }

    private static <T> Node<T> nesteInorden(Node<T> p){
        if(p.høyre != null){
            if(p.høyre.venstre == null)
                return p.høyre;
            else{
                Node<T> peker = p.høyre.venstre;
                while(peker.venstre != null){
                    peker = peker.venstre;
                }
                return peker;
            }
        }
        else if(p.forelder == null) {
            return null;
        }
        else if(p.forelder.høyre == null) {
            return p.forelder;
        }
        else if(p.forelder.høyre.equals(p)) {
            Node<T> peker = p;
            while (peker.forelder != null){
                if (peker.forelder.venstre != null && peker.forelder.venstre.equals(peker)){
                    return peker.forelder;
                }else {
                    peker = peker.forelder;
                }
            }
            return peker.forelder;
        }
        else {
            return p.forelder;
        }
    }

    @Override
    public String toString() {
        if (rot == null)
            return "[]";
        if(rot.høyre == null & rot.venstre == null)
            return "[" + rot.verdi + "]";
        String r = "[";
        Node<T> i = rot;
        while(i.venstre != null)
            i = i.venstre;
        while(nesteInorden(i) != null){
            r = r + i.verdi + ", ";
            i = nesteInorden(i);
        }
        r = r + i.verdi + ", ";
        r = r.substring(0, r.length()-2);
        return r + "]";
    }

    public String omvendtString()
    {
        if(rot == null){
            return "[]";
        }
        
        Deque<Node<T>> stakk = new ArrayDeque<Node<T>>();
        Deque<Node<T>> hjelp = new ArrayDeque<Node<T>>();
        StringBuilder build = new StringBuilder();
        int ant = antall();
        int gjennom = 0;
        Node<T> plass = rot;
        
        if(plass.høyre == null){
            gjennom++;
            build.append("[" + plass.verdi + ", ");
        }else{
            build.append("[");
        }
        
        for(int i = gjennom; i < ant; i++){
            if(!hjelp.contains(plass.høyre) && plass.høyre != null){
                while(plass.høyre != null){
                    stakk.push(plass);
                    plass = plass.høyre;
                }
                build.append(plass.verdi + ", ");
                hjelp.push(plass);
                
            }else if(!hjelp.contains(plass.venstre) && plass.venstre != null){
                stakk.push(plass);
                plass = plass.venstre;
                while(plass.høyre != null){
                    stakk.push(plass);
                    plass = plass.høyre;
                }
                build.append(plass.verdi + ", ");
                hjelp.push(plass);
                
            }else if(stakk.peek().høyre == plass){
                plass = stakk.pop();
                hjelp.push(plass);
                build.append(plass.verdi + ", ");
            
            }else if(stakk.peek().venstre == plass){
                while(stakk.peek().venstre == plass){
                    plass = stakk.pop();
                }
                plass = stakk.pop();
                hjelp.push(plass);
                build.append(plass.verdi + ", ");
            }
        }
        
        build.delete(build.length()-2, build.length());
        build.append("]");
        
        String s = build.toString();
        
        return s;
    }

    public String høyreGren()
    {
        if(antall == 0){
            return "[]";
        }
        
        Node<T> plass = rot;
        StringBuilder build = new StringBuilder();
        build.append("[").append(plass.verdi).append(", ");
        
        while(plass.høyre != null || plass.venstre != null){
            if(plass.høyre != null){
                plass = plass.høyre;
            }else{
                plass = plass.venstre;
            }
            build.append(plass.verdi).append(", ");
        }
        
        build.delete(build.length()-2, build.length()).append("]");
        String s = build.toString();
        return s;
    }

    public String lengstGren()
    {
        if(antall == 0){
            return "[]";
        }else if(antall == 1){
            return "["+rot.verdi+"]";
        }
        
        Deque<Node<T>> lengste = new ArrayDeque<Node<T>>();
        Deque<Node<T>> proto = new ArrayDeque<Node<T>>();
        Deque<Node<T>> testet = new ArrayDeque<Node<T>>();
        Node<T> plass = rot;
        int rotCount = 0;
        if(rot.venstre == null && rot.høyre != null){
            rotCount++;
        }else if(rot.høyre == null && rot.venstre != null){
            rotCount++;
        }
        
        proto.push(rot);
        
        while(rotCount < 2){
            if((testet.contains(plass.venstre) && testet.contains(plass.høyre)) || 
                    ((testet.contains(plass.høyre) && plass.venstre == null) ||
                    testet.contains(plass.venstre) && plass.høyre == null) || 
                    (plass.høyre == null && plass.venstre == null)){
                testet.push(plass);
                if(proto.size() > lengste.size()){
                    lengste = new ArrayDeque<>(proto);
                }
                proto.pop();
                plass = plass.forelder;
            }else if(!testet.contains(plass.venstre) && plass.venstre != null){
                plass = plass.venstre;
                proto.push(plass);
            }else if(!testet.contains(plass.høyre) && plass.høyre != null){
                plass = plass.høyre;
                proto.push(plass);
            }
            if(plass == rot){
                rotCount++;
            }
        }
        
        StringBuilder build = new StringBuilder();
        build.append("]");
        while(lengste.peek() != null){
            build.insert(0, lengste.pop()).insert(0, ", ");
        }
        build.delete(0, 2);
        build.insert(0, "[");
        
        return build.toString();
    }

    public String[] grener()
    {
        if(antall == 0){
            return new String[] {};
        }else if(antall == 1){
            return new String[] {"[" + rot.verdi + "]"};
        }
        
        Deque<Node<T>> proto = new ArrayDeque<Node<T>>();
        Deque<Node<T>> testet = new ArrayDeque<Node<T>>();
        Deque<Node<T>> overfor = new ArrayDeque<Node<T>>();
        Deque<String> sa = new ArrayDeque<String>();
        Node<T> plass = rot;
        int rotCount = 0;
        if(rot.venstre == null && rot.høyre != null){
            rotCount++;
        }else if(rot.høyre == null && rot.venstre != null){
            rotCount++;
        }
        
        proto.push(rot);
        
        while(rotCount < 2){
            if((testet.contains(plass.venstre) && testet.contains(plass.høyre)) || 
                    ((testet.contains(plass.høyre) && plass.venstre == null) ||
                    testet.contains(plass.venstre) && plass.høyre == null)){
                testet.push(plass);
                proto.pop();
                plass = plass.forelder;
            }else if(plass.høyre == null && plass.venstre == null){
                testet.push(plass);
                overfor = new ArrayDeque<>(proto);
                StringBuilder build = new StringBuilder();
                build.append("]");
                while(overfor.peek() != null){
                    build.insert(0, overfor.pop()).insert(0, ", ");
                }
                build.delete(0, 2);
                build.insert(0, "[");
                sa.push(build.toString());
                proto.pop();
                plass = plass.forelder;
            }else if(!testet.contains(plass.venstre) && plass.venstre != null){
                plass = plass.venstre;
                proto.push(plass);
            }else if(!testet.contains(plass.høyre) && plass.høyre != null){
                plass = plass.høyre;
                proto.push(plass);
            }
            if(plass == rot){
                rotCount++;
            }
        }
        
        int size = sa.size();
        String[] s = new String[size];
        
        while(sa.peek() != null){
            s[size - 1] = sa.pop();
            size--;
        }
        
        return s;
    }

    public String bladnodeverdier()
    {
        if(antall == 0){
            return "[]";
        }else if(antall == 1){
            return "["+rot.verdi+"]";
        }
        Deque<Node<T>> testet = new ArrayDeque<>();
        Deque<Node<T>> blader = new ArrayDeque<>();
        Node<T> plass = rot;
        
        blader = bladHjelp(blader, testet, plass);
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while(blader.peek() != null){
            sb.insert(1, blader.pop().verdi).insert(1, ", ");
        }
        sb.delete(1, 3).append("]");
        
        return sb.toString();
    }
    
    private Deque<Node<T>> bladHjelp(Deque<Node<T>> blader, Deque<Node<T>> testet, Node<T> plass){
        if(!testet.contains(plass.venstre) && plass.venstre != null){
            plass = plass.venstre;
            bladHjelp(blader, testet, plass);
        }else if(!testet.contains(plass.høyre) && plass.høyre != null){
            plass = plass.høyre;
            bladHjelp(blader, testet, plass);
        }else if(plass.høyre == null && plass.venstre == null){
            blader.push(plass);
            testet.push(plass);
            plass = plass.forelder;
            bladHjelp(blader, testet, plass);
        }else if(testet.contains(plass.venstre) && testet.contains(plass.venstre) && plass != rot){
            testet.push(plass);
            plass = plass.forelder;
            bladHjelp(blader, testet, plass);
        }else if((testet.contains(plass.venstre) && plass.høyre == null && plass != rot) || 
                (testet.contains(plass.høyre) && plass.venstre == null && plass != rot)){
            testet.push(plass);
            plass = plass.forelder;
            bladHjelp(blader, testet, plass);
        }
        return blader;
    }

    public String postString()
    {
        if(antall == 0){
            return "[]";
        }else if(antall == 1){
            return "["+rot.verdi+"]";
        }
        Deque<Node<T>> testet = new ArrayDeque<>();
        List<Node<T>> orden = new ArrayList<>();
        Node<T> plass = rot;
        int rotCount = 0;
        if(rot.venstre == null && rot.høyre != null){
            rotCount++;
        }else if(rot.høyre == null && rot.venstre != null){
            rotCount++;
        }
        
        while(rotCount < 2){
            if(!testet.contains(plass.venstre) && plass.venstre != null){
                plass = plass.venstre;
            }else if(!testet.contains(plass.høyre) && plass.høyre != null){
                plass = plass.høyre;
            }else if((testet.contains(plass.venstre) && testet.contains(plass.høyre)) ||
                    (testet.contains(plass.venstre) && plass.høyre == null) ||
                    (testet.contains(plass.høyre) && plass.venstre == null) ||
                    (plass.venstre == null && plass.høyre == null)){
                testet.push(plass);
                plass = plass.forelder;
            }
            if(plass == rot){
                rotCount++;
                if(rotCount == 2){
                    testet.push(rot);
                }
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while(testet.peek() != null){
            sb.append(testet.pollLast()).append(", ");
        }
        sb.delete(sb.length()-2, sb.length());
        sb.append("]");
        
        
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator()
    {
        return new BladnodeIterator();
    }

    private class BladnodeIterator implements Iterator<T>
    {
        private Node<T> p = rot, q = null;
        private boolean removeOK = false;
        private int iteratorendringer = endringer;

        private BladnodeIterator()  // konstruktør
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public boolean hasNext()
        {
            return p != null; // Denne skal ikke endres!
        }

        @Override
        public T next()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }
    } // BladnodeIterator
} // ObligSBinTre
