import java.awt.Color;
public class BuracoNegro implements ElementoMapa {
    private Color cor;
    private Character simbolo;

    public BuracoNegro(Character simbolo, Color cor) {
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
        return "AVISO!!! Você está próximo à um buraco negro. Não caia, senão irá morrer e perder o jogo";
    }

    
}

    

