// MainCanodromo.java
package arsw.threads;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MainCanodromo {

    private static Galgo[] galgos;
    private static Canodromo can;
    private static RegistroLlegada reg = new RegistroLlegada();

    public static void main(String[] args) {
        can = new Canodromo(17, 100);
        galgos = new Galgo[can.getNumCarriles()];
        can.setVisible(true);

        // START
        can.setStartAction(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JButton btn = (JButton) e.getSource();
                btn.setEnabled(false);

                new Thread(() -> {
                    // 1) Iniciar galgos
                    for (int i = 0; i < can.getNumCarriles(); i++) {
                        galgos[i] = new Galgo(can, can.getCarril(i), "" + i, reg);
                        galgos[i].start();
                    }
                    // 2) Esperar fin de todos
                    for (Galgo g : galgos) {
                        try {
                            g.join();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    // 3) Mostrar resultado
                    can.winnerDialog(reg.getGanador(),
                            reg.getUltimaPosicionAlcanzada() - 1);
                    btn.setEnabled(true);
                }).start();
            }
        });

        // STOP → pausa
        can.setStopAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                can.pauseRace();
                System.out.println("Carrera pausada!");
            }
        });

        // CONTINUE → reanuda
        can.setContinueAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                can.resumeRace();
                System.out.println("Carrera reanudada!");
            }
        });
    }
}
