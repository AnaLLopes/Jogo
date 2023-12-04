import java.awt.Color;
public class MinaExplosiva implements ElementoMapa {
    private Color cor;
    private Character simbolo;

    public MinaExplosiva(Character simbolo, Color cor) {
        this.simbolo = simbolo;
        this.cor = cor;
    }
    
    public Character getSimbolo() {
        return simbolo;
    }

    public Color getCor() {
        return cor;
    }

    @Override
    public boolean podeSerAtravessado() {
        return true;
    }

    @Override
    public boolean podeInteragir() {
        return true;
    }

    @Override
    public String interage() {
        return "AVISO!!! Mina explosiva no perimêtro. CUIDADO!";
    }
}

    

