import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
public abstract class MenuComponent {
    protected String name;

    public MenuComponent(String name) {
        this.name = name;
    }

    public abstract void display();


    public void add(MenuComponent menuComponent) {
        throw new UnsupportedOperationException();
    }

    public void remove(MenuComponent menuComponent) {
        throw new UnsupportedOperationException();
    }

    public MenuComponent getChild(int index) {
        throw new UnsupportedOperationException();
    }
}




 class Menu extends MenuComponent {
    private List<MenuComponent> menuComponents;

    public Menu(String name) {
        super(name);
        menuComponents = new ArrayList<>();
    }

    public void display() {
        System.out.println(name);
        for (MenuComponent component : menuComponents) {
            component.display();
        }
    }

    public void add(MenuComponent menuComponent) {
        menuComponents.add(menuComponent);
    }

    public void remove(MenuComponent menuComponent) {
        menuComponents.remove(menuComponent);
    }

    public MenuComponent getChild(int index) {
        return menuComponents.get(index);
    }
}
class MenuItem extends MenuComponent {
    public MenuItem(String name) {
        super(name);
    }

    public void display() {
        System.out.println(name);
    }
}


