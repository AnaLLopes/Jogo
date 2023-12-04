import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Mapa {
    private List<String> mapa;
    private Map<Character, ElementoMapa> elementos;
    private int x = 50; // Posição inicial X do personagem
    private int y = 50; // Posição inicial Y do personagem
    private final int TAMANHO_CELULA = 10; // Tamanho de cada célula do mapa
    private boolean[][] areaRevelada; // Rastreia quais partes do mapa foram reveladas
    private final Color brickColor = new Color(153, 76, 0); // Cor marrom para tijolos
    private final Color vegetationColor = new Color(34, 139, 34); // Cor verde para vegetação
    private final Color purple = new Color(128, 0, 128); // Cor roxa para buraco negro
    private final Color red = new Color(255, 0, 0); // Cor vermelha para mina explosiva
    private final int RAIO_VISAO = 5; // Raio de visão do personagem

    public Mapa(String arquivoMapa) {
        mapa = new ArrayList<>();
        elementos = new HashMap<>();
        registraElementos();
        carregaMapa(arquivoMapa);
        areaRevelada = new boolean[mapa.size()+1000][mapa.get(0).length()+1000];
        atualizaCelulasReveladas();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTamanhoCelula() {
        return TAMANHO_CELULA;
    }

    public int getNumLinhas() {
        return mapa.size();
    }

    public int getNumColunas() {
        return mapa.get(0).length();
    }

    public ElementoMapa getElemento(int x, int y) {
        Character id = mapa.get(y).charAt(x);
        return elementos.get(id);
    }

    public boolean estaRevelado(int x, int y) {
        return areaRevelada[y][x];
    }

    // Move conforme enum Direcao
    public boolean move(Direcao direcao) {
        int dx = 0, dy = 0;

        switch (direcao) {
            case CIMA:
                dy = -TAMANHO_CELULA;
                break;
            case BAIXO:
                dy = TAMANHO_CELULA;
                break;
            case ESQUERDA:
                dx = -TAMANHO_CELULA;
                break;
            case DIREITA:
                dx = TAMANHO_CELULA;
                break;
            default:
                return false;
        }
        
        if (!podeMover(x + dx, y + dy)) {
            System.out.println("Não pode mover");
            return false;
        }

        x += dx;
        y += dy;

        // Atualiza as células reveladas
        atualizaCelulasReveladas();
        return true;
    }

    // Verifica se o personagem pode se mover para a próxima posição
    private boolean podeMover(int nextX, int nextY) {
        int mapX = nextX / TAMANHO_CELULA;
        int mapY = nextY / TAMANHO_CELULA - 1;

        if (mapa == null)
            return false;

        if (mapX >= 0 && mapX < mapa.get(0).length() && mapY >= 1 && mapY <= mapa.size()) {
            char id;

            try {
               id = mapa.get(mapY).charAt(mapX);
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            }

            if (id == ' ')
                return true;

            ElementoMapa elemento = elementos.get(id);
            if (elemento != null) {
                //System.out.println("Elemento: " + elemento.getSimbolo() + " " + elemento.getCor());
                return elemento.podeSerAtravessado();
            }
        }

        return false;
    }

    public List<ElementoMapa> elementosRaioDuasCelulas()
    {
        List<ElementoMapa> elementosPerto =  new ArrayList<>();

        int mapX = this.getX()/TAMANHO_CELULA;
        int mapY = this.getY()/TAMANHO_CELULA;

        for (int i = Math.max(0, mapY - 3); i <= Math.min(this.getNumLinhas(), mapY+ 2); i++) { //For para delimitar os elementos que estao em cima e embaixo. A direção é de cima para baixo
            for (int j = Math.max(0, mapX - 3); j <= Math.min(this.getNumColunas(), mapX + 2); j++) { //For para delimitar os elementos que estao na esquerda e na direita. A diração é da esquerda para direita
                if (this.estaRevelado(j, i)) {
                    ElementoMapa elementoMapa = this.getElemento(j, i); //Se não for nulo e poder interagir
                    if (elementoMapa != null && elementoMapa.podeInteragir()) { //adiciona lista
                        elementosPerto.add(elementoMapa);
                        elementosPerto.sort(new Comparator<ElementoMapa>() { //Chama o método sort antes de retornar a lista para que ela fique na ordem de prefêrencia. A prefêrencia são minas e depois buracos
                            public int compare(ElementoMapa e1, ElementoMapa e2) {
                            if (e1 instanceof MinaExplosiva && !(e2 instanceof MinaExplosiva)) { 
                                return -1;
                            } else if (!(e1 instanceof MinaExplosiva) && e2 instanceof MinaExplosiva) {
                                return 1;
                            } else {

                                return 0;
                            }
                            }
                        });
                    }
                }
            }
        }
        

        return elementosPerto;
    }

    public String interage() {
        if(mapa == null)
         {return null;}

        List<ElementoMapa> elementosperto = elementosRaioDuasCelulas();

        if(elementosperto.isEmpty())
        {
            return "Não há elementos por perto para interagir! Se aproxime de um e tente novamente";
        }
            return elementosperto.get(0).interage();
    }

    public String ataca() {

        
        return "Ataca";
    }

    private void carregaMapa(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                mapa.add(line);
                // Se character 'P' está contido na linha atual, então define a posição inicial do personagem
                if (line.contains("P")) {
                    x = line.indexOf('P') * TAMANHO_CELULA;
                    y = mapa.size() * TAMANHO_CELULA;
                    // Remove o personagem da linha para evitar que seja desenhado
                    mapa.set(mapa.size() - 1, line.replace('P', ' '));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar as células reveladas
    private void atualizaCelulasReveladas() {
        if (mapa == null)
            return;
        for (int i = Math.max(0, y / TAMANHO_CELULA - RAIO_VISAO); i < Math.min(mapa.size(), y / TAMANHO_CELULA + RAIO_VISAO + 1); i++) {
            for (int j = Math.max(0, x / TAMANHO_CELULA - RAIO_VISAO); j < Math.min(mapa.get(i).length(), x / TAMANHO_CELULA + RAIO_VISAO + 1); j++) {
                areaRevelada[i][j] = true;
            }
        }
    }

    // Registra os elementos do mapa
    private void registraElementos() {
        // Parede
        elementos.put('#', new Parede('▣', brickColor));
        // Vegetação
        elementos.put('V', new Vegetacao('♣', vegetationColor));
        elementos.put('B', new BuracoNegro('۝', purple));
        elementos.put('M', new MinaExplosiva('߷', red));
    }

    
    
}
