package utils;

import core.Dialogs;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.requirements_types_editor.RequirementType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;


public class Utils {

    public static void clearControls(AnchorPane root) {
        for (Node n : root.getChildren()) {
            if (n instanceof TextField) ((TextField) n).clear();
            if (n instanceof TextArea) ((TextArea) n).clear();
            if (n instanceof ComboBox) ((ComboBox) n).getEditor().setText("");
            if (n instanceof DatePicker) ((DatePicker) n).getEditor().setText("");
            if (n instanceof CheckBox) ((CheckBox) n).setSelected(false);
        }
    }

    public static boolean hasEmptyControls(AnchorPane root, String... exceptions) {
        boolean hasEmpty = false;
        for (Node n : root.getChildren()) {
            if (n.getId() != null) {
                if (Arrays.asList(exceptions).indexOf(n.getId()) >= 0) continue;

                if (n instanceof TextField)
                    if (((TextField) n).getText().length() == 0) hasEmpty = true;

                if (n instanceof TextArea)
                    if (((TextArea) n).getLength() == 0) hasEmpty = true;

                if (n instanceof ComboBox)
                    if (((ComboBox) n).isEditable()) {
                        if (((ComboBox) n).getEditor().getText().length() == 0) hasEmpty = true;
                    } else {
                        if ((((ComboBox) n).getSelectionModel().getSelectedIndex() < 0)) hasEmpty = true;
                    }

                if (n instanceof DatePicker)
                    if (((DatePicker) n).getEditor().getText().length() == 0) hasEmpty = true;

                if (n instanceof ListView)
                    if (((ListView) n).getItems().size() == 0) hasEmpty = true;
            }
        }

        if (hasEmpty) {
            Dialogs.showMessage("Пустые значения", "Не все поля заполнены");
            return true;
        }

        return false;
    }

    public static void setColor(AnchorPane root, String id, Color color) {
        for (Node n : root.getChildren()) {
            if (n.getId() != null) {
                if (n.getId().equals(id)) {
                    if (n instanceof TextField) {
                        if (color == Color.RED) n.setStyle("-fx-text-inner-color: red;");
                        if (color == Color.GREEN) n.setStyle("-fx-text-inner-color: green;");
                    }
                }
            }
        }
    }

    public static String getControlValue(AnchorPane root, String id) {
        for (Node n : root.getChildren()) {
            if (n.getId() != null && n.getId().equals(id)) {
                if (n instanceof TextField) return ((TextField) n).getText();
                if (n instanceof TextArea) return ((TextArea) n).getText();
                if (n instanceof ComboBox) {
                    int index = ((ComboBox) n).getSelectionModel().getSelectedIndex();
                    return index >= 0 ? (String) ((ComboBox) n).getItems().get(index) : "";
                }
                if (n instanceof DatePicker) return ((DatePicker) n).getEditor().getText();
                if (n instanceof CheckBox) return ((CheckBox) n).isSelected() == true ? "true" : "false";
                if (n instanceof ListView) {
                    ListView<String> lv = (ListView<String>) n;

                    String result = "";
                    int i;
                    if (lv.getItems().size() > 0) result = lv.getItems().get(0);
                    for (i = 1; i < ((ListView<String>) n).getItems().size(); i++) {
                        result += "," + lv.getItems().get(i);
                    }
//                    if (lv.getItems().size() > 1) result += lv.getItems().get(++i);

                    return result;
                }
            }
        }
        return "";
    }

    public static ArrayList<String> getALControlValueFromLV(AnchorPane root, String id) {
        ArrayList<String> result = new ArrayList<>();
        for (Node n : root.getChildren()) {
            if (n.getId() != null && n.getId().equals(id)) {
                if (n instanceof ListView) {
                    result.addAll(((ListView<String>) n).getItems());
                }
            }
        }
        return result;
    }

    public static void setControlValueLVfromAL(AnchorPane root, String id, ArrayList<String> items) {
        if (items == null) return;

        for (Node n : root.getChildren()) {
            if (n.getId() != null && n.getId().equals(id)) {//id matches
                if (n instanceof ListView) {
                    ((ListView) n).getItems().addAll(items);
                }
            }
        }
    }

    public static void addControlValueLV(AnchorPane root, String id, String item) {
        System.out.println("adding to " + id + " item " + item);
        for (Node n : root.getChildren()) {
            if (n.getId() != null && n.getId().equals(id)) {//id matches
                if (n instanceof ListView) {
                    ((ListView) n).getItems().add(item);
                    break;
                }
            }
        }
    }

    public static void setControlValue(AnchorPane root, String id, String value) {
        TextArea ta = null;
        for (Node n : root.getChildren()) {
            if (n.getId() != null && n.getId().equals(id)) {//id matches
                if (n instanceof TextField) {
                    ((TextField) n).setText(value);

                } else if (n instanceof TextArea) {
                    ta = ((TextArea) n);
                    if (ta.getText().length() > 0) ta.appendText(", " + value);
                    else ta.setText(value);

                } else if (n instanceof ComboBox) {
                    if (((ComboBox) n).isEditable()) {
                        ((ComboBox) n).getEditor().setText(value);
                    } else {
//                        ((ComboBox) n).getSelectionModel().select(Integer.parseInt(value));
                        ((ComboBox) n).getSelectionModel().select(((ComboBox) n).getItems().indexOf(value));
                    }

                } else if (n instanceof ListView) {
                    if (value != null && value.length() > 0) {
                        ((ListView) n).getItems().clear();

                        ((ListView) n).getItems().addAll(value.split("\\|"));
                    }


                } else if (n instanceof DatePicker) {
                    ((DatePicker) n).getEditor().setText(value);
                } else if (n instanceof Label) {
                    ((Label) n).setText(value);
                }
            }
        }
    }

    public static void setControlValue(AnchorPane root, String id, boolean value) {
        for (Node n : root.getChildren()) {
            if (n instanceof CheckBox)
                if (n.getId().equals(id))
                    ((CheckBox) n).setSelected(value);
        }
    }

    public static void setControlValue(AnchorPane root, String id, ArrayList<?> items) {
        if (items == null) return;
        ComboBox<String> cb = null;
        for (Node n : root.getChildren()) {
            if (n.getId() != null && n.getId().equals(id)) {
                if (n instanceof ComboBox) {

                    cb = (ComboBox) n;

                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i) instanceof RequirementType) {
                            RequirementType ct = (RequirementType) items.get(i);
                            cb.getItems().add(ct.getShortName() + " (" + ct.getFullName() + ")");
                        } else if (items.get(i) instanceof String) {
                            cb.getItems().add((String) items.get(i));
                        }
                    }
                } else if (n instanceof ListView) {
                    ((ListView) n).getItems().clear();
                    ((ListView) n).getItems().addAll(items);
                }
            }
        }
    }

    public static int getControlSelectedValue(AnchorPane root, String id) {
        for (Node n : root.getChildren()) {
            if (n instanceof ComboBox) {
                if (n.getId() != null && n.getId().equals(id)) {
                    return ((ComboBox) n).getSelectionModel().getSelectedIndex();
                }
            }
        }
        return -1;
    }

    public static void setControlSelectedValue(AnchorPane root, String id, int index) {
        for (Node n : root.getChildren()) {
            if (n instanceof ComboBox) {
                if (n.getId() != null && n.getId().equals(id)) {
                    ((ComboBox) n).getSelectionModel().select(index);
                }
            }
        }
    }

    public static String getDateTime() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
    }

    public static String getDate(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    public static Date getDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date result = null;

        try {
            result = formatter.parse(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public static String toEN(String value) {
        if (value == null) return "";

        String[] inLetters = new String[]{"А", "В", "С", "Е", "Н", "К", "М", "О", "Р", "Т", "Х"};
        String[] outLetters = new String[]{"A", "B", "C", "E", "H", "K", "M", "O", "P", "T", "X"};

        int pos;
        String result = "";
        for (int i = 0; i < value.length(); i++) {
            pos = Arrays.asList(inLetters).indexOf(Character.toString(value.charAt(i)));

            result = pos > -1 ? result + outLetters[pos] : result + value.charAt(i);
        }

        return result;
    }

    public static ArrayList<String> stringToList(String text) {
        ArrayList<String> temp = new ArrayList<>();

        if (text == null) return temp;

        String[] tempArr = text.split("\\,");
        for (String s : tempArr) {
            temp.add(s.trim());
        }
        return temp;
    }

    public static String listToString(List<String> list) {
        String temp = "";
        if (list.size() > 0) temp = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            temp += "," + list.get(i).trim();
        }
        return temp;
    }

    public static String getValueInBrackets(String line) {
        if (line.length() > 0) return line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        else return "";
    }

    public static String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "Unknown Computer";
    }

    public static void disableEditing(Object object, boolean value) {
        AnchorPane ap = null;

        if (object instanceof Tab) ap = (AnchorPane) ((Tab) object).getContent();
        else if (object instanceof AnchorPane) ap = (AnchorPane) object;

        for (Node n : ap.getChildren()) {

            if (n == null) continue;

            if (n instanceof TableView) {
                TableView tv = ((TableView) n);
                ContextMenu cm = tv.getContextMenu();

                if (tv.isEditable()) tv.setEditable(!value);

                if (cm != null) {
                    for (MenuItem mi : cm.getItems()) {
                        mi.setDisable(value);
                    }
                }
            } else if (n instanceof Button) {
                if (n.getId() != null && n.getId().toLowerCase().endsWith("apply")) n.setDisable(value);
            }
        }
    }

    public static void openFile(File file) {
        if (Desktop.isDesktopSupported() && file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                System.out.println("open file exception " + e.getMessage());
            }
        }
    }

    public static Tab getTabById(String id) {
        for (Tab tab : OptionsWindow.getTpOptions().getTabs()) {
            if (tab.getId().equals(id)) return tab;
        }
        return null;
    }

    public static void disableMenuItemsButton(Object object, String id, String... itemsExceptions) {
        AnchorPane ap = null;

        if (object instanceof Tab) ap = (AnchorPane) ((Tab) object).getContent();
        else if (object instanceof AnchorPane) ap = (AnchorPane) object;

        for (Node n : ap.getChildren()) {
            if (n instanceof TableView) {
                ContextMenu cm = ((TableView) n).getContextMenu();
                if (cm != null && cm.getId().equals(id)) {

                    for (MenuItem mi : cm.getItems()) {
                        if (Arrays.asList(itemsExceptions).indexOf(mi.getId()) >= 0) continue;

                        mi.setDisable(true);
                    }

                }
            } else if (n instanceof Button) {
                if (n != null && n.getId() != null) {
                    if (n.getId().equals(id)) n.setDisable(true);
                }
            }
        }
    }

    public static void copyFilsToClipboard(ArrayList<File> files) {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        HashSet<File> resFiles = new HashSet<>(files);
        ArrayList<File> filesForCopy = new ArrayList<>(resFiles);

        ClipboardContent cc = new ClipboardContent();
        cc.putFiles(filesForCopy);
        clipboard.setContent(cc);
        String filesName = "";
        for (File file : filesForCopy) {
            filesName += " - " + file.getName() + "\n";
        }
        Dialogs.showMessage("Буфер обмена", "Следующие файлы были вставлены в буфер обмена:\n" + filesName);
    }


    public static void deleteFolder(Path pathForDeleting) throws IOException {
        Files.walk(pathForDeleting)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

//        if (Files.exists(pathForDeleting)) Dialogs.showMessage("Удаление временной папки",
//                "Не удалось удалить временную папку\n" + pathForDeleting);

    }

    public static String addTextWithCommas(String target, String text) {
        if (text == null || text.isEmpty()) return target;

        if (target == null || target.isEmpty()) {
            target = text;
            return text;
        }

        return target.concat(",").concat(text);
    }

    public static ArrayList<Integer> getNumberALfromStringEnum(String numbersEnum) {
        ArrayList<Integer> result = new ArrayList<>();
        if (numbersEnum == null || numbersEnum.trim().isEmpty()) return result;

        String[] numbers = numbersEnum.split("\\,");
        for (String numberS : numbers) {
            if (numberS.trim().matches("^\\d+$")) {
                result.add(Integer.parseInt(numberS));
            }
        }

        return result;
    }

    public static Node getFxControl(AnchorPane root, String id) {
        for (Node n : root.getChildren()) {
            if (n.getId().toLowerCase().equals(id.toLowerCase())) return n;
        }
        return null;
    }
}
