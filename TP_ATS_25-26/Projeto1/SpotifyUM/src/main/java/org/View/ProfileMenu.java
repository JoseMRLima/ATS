package org.View;

import org.Controller.Controller;

/**
 * Classe que representa o menu de perfil do utilizador.
 * Permite aceder e editar dados do perfil e consultar estatísticas.
 */
public class ProfileMenu extends Menu {
    /**
     * Construtor do menu de perfil.
     * @param controller Controlador principal da aplicação.
     * @param menuManager Gestor de menus.
     */
    public ProfileMenu(Controller controller, MenuManager menuManager) {
        super(controller, menuManager);
    }

    /**
     * Mostra o menu de perfil e trata a seleção do utilizador.
     */
    @Override
    public void show() {
        System.out.println("\n👤━━━━━━━━━━━━━━━【 MENU DE PERFIL 】━━━━━━━━━━━━━━━👤\n");
        System.out.printf("%-5s %s\n", "1️⃣", "🔍 Ver Perfil");
        System.out.printf("%-5s %s\n", "2️⃣", "✏️  Editar Perfil");
        System.out.printf("%-5s %s\n", "3️⃣", "🔄 Alterar Plano");
        System.out.printf("%-5s %s\n", "0️⃣", "🔙 Voltar ao Menu Principal");
        System.out.print("\n👉 Escolha a sua opção: ");
        handleInput();
    }

    /**
     * Lê e processa a opção escolhida pelo utilizador.
     */
    @Override
    public void handleInput() {
        try{
            int choice = getScanner().nextInt();
            getScanner().nextLine();
            cleanTerminal();

            switch (choice) {
                case 1:
                    System.out.println(getController().getCurrentUser());
                    break;
                case 2:
                    System.out.println("✏️ A redirecionar para o editor de perfil...");
                    getMenuManager().setMenu(new ProfileEditorMenu(getController(), getMenuManager()));
                    break;
                case 3:
                    
                    getMenuManager().setMenu(new PlanMenu(getController(), getMenuManager()));

                    break;
                case 0:
                    System.out.println("🔙 A voltar ao menu principal...");
                    getMenuManager().setMenu(new MainMenu(getController(), getMenuManager()));
                    break;
                default:
                    System.out.println("⚠️ Opção inválida. Tente novamente.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Por favor, insira apenas Números.");
            getScanner().nextLine(); // limpar buffer
        }
    }
}
