import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

public class ButtonEditor implements TableCellEditor {

    Loto loto;

    public ButtonEditor(Loto loto) {
        this.loto = loto;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        loto.buttons[row].doClick();
        return loto.buttons[row];
    }

    public Object getCellEditorValue() {
        return null;
    }

    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    public boolean stopCellEditing() {
        return true;
    }

    public void cancelCellEditing() {

    }

    public void addCellEditorListener(CellEditorListener l) {

    }

    public void removeCellEditorListener(CellEditorListener l) {

    }
}
