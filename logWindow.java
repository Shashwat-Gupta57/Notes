import javax.swing.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import jnafilechooser.api.*;
import java.util.concurrent.*;
import java.util.Scanner;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
public class logWindow extends JFrame {
    private static final AtomicInteger windowCount = new AtomicInteger(0);
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private Font font = null;
    private File currentFile = null;
    private boolean isModified = false;
    private UndoManager undoManager;
    private JLabel statusLabel;
    Boolean isOpen = false,isLog = true;
    private float defaultFontSize = 16.0f;
    private JnaFileChooser ch = new JnaFileChooser();
    public logWindow() {
        windowCount.incrementAndGet(); // Increment the window count when a new window is created
        undoManager = new UndoManager();
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "\\res\\Consolas.TTF"));
            font = font.deriveFont(defaultFontSize);
        } catch (FontFormatException | IOException ffe) {
            ffe.printStackTrace();
        }
        setTitle("*log.txt - Notes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle closing manually
        setIconImage(new ImageIcon(System.getProperty("user.dir")+"\\res\\icon.png").getImage());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isOpen = false;
                setVisible(false);
            }
        });

        textArea = new JTextArea();
        textArea.setFont(font);
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!isModified) {
                    isModified = true;
                    updateTitle();
                }
                updateStatusBar();
            }
        });
        textArea.addCaretListener(e -> updateStatusBar());

        fileChooser = new JFileChooser();

        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = createMenuItem("New", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        JMenuItem newWindowItem = createMenuItem("New Window", KeyEvent.VK_W, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        JMenuItem openItem = createMenuItem("Open", KeyEvent.VK_O, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        JMenuItem saveItem = createMenuItem("Save", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        JMenuItem saveAsItem = createMenuItem("Save As", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        JMenuItem exitItem = createMenuItem("Exit", KeyEvent.VK_E, null);

        newItem.addActionListener(e -> newFile());
        newWindowItem.addActionListener(e -> newWindow());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        saveAsItem.addActionListener(e -> saveFileAs());
        exitItem.addActionListener(e -> {
            if (promptForUnsavedChanges()) {
                if (windowCount.decrementAndGet() == 0) { // Decrement window count and check if it's zero
                    System.exit(0); // Exit program if no more windows are open
                }
            }
        });

        fileMenu.add(newItem);
        fileMenu.add(newWindowItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        //fileMenu.addSeparator();
        //fileMenu.add(printItem);
        //fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = createMenuItem("Undo", KeyEvent.VK_U, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        JMenuItem redoItem = createMenuItem("Redo", KeyEvent.VK_R, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        JMenuItem cutItem = createMenuItem("Cut", KeyEvent.VK_T, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        JMenuItem copyItem = createMenuItem("Copy", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        JMenuItem pasteItem = createMenuItem("Paste", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        JMenuItem findItem = createMenuItem("Find", KeyEvent.VK_F, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        JMenuItem replaceItem = createMenuItem("Replace", KeyEvent.VK_R, KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        JMenuItem goToItem = createMenuItem("Go To", KeyEvent.VK_G, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        JMenuItem selectAllItem = createMenuItem("Select All", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        JMenuItem timeDateItem = createMenuItem("Time/Date", KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        JMenuItem changeFontItem = createMenuItem("Change Font", KeyEvent.VK_F, null);
        
        changeFontItem.addActionListener(e -> changeFont());
        undoItem.addActionListener(e -> undoManager.undo());
        redoItem.addActionListener(e -> undoManager.redo());
        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        findItem.addActionListener(e -> showFindDialog());
        replaceItem.addActionListener(e -> showReplaceDialog());
        goToItem.addActionListener(e -> goToLine());
        selectAllItem.addActionListener(e -> textArea.selectAll());
        timeDateItem.addActionListener(e -> insertTimeDate());

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(findItem);
        editMenu.add(replaceItem);
        editMenu.add(goToItem);
        editMenu.add(selectAllItem);
        editMenu.add(timeDateItem);        
        editMenu.add(changeFontItem);
        menuBar.add(editMenu);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem increaseFontSizeItem = createMenuItem("Increase Font Size", KeyEvent.VK_I, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        JMenuItem decreaseFontSizeItem = createMenuItem("Decrease Font Size", KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        JMenuItem resetFontSizeItem = createMenuItem("Reset Font Size", KeyEvent.VK_R, null);

        increaseFontSizeItem.addActionListener(e -> changeFontSize(1));
        decreaseFontSizeItem.addActionListener(e -> changeFontSize(-1));
        resetFontSizeItem.addActionListener(e -> resetFontSize());
        
        viewMenu.add(increaseFontSizeItem);
        viewMenu.add(decreaseFontSizeItem);
        viewMenu.add(resetFontSizeItem);
        menuBar.add(viewMenu);
        
        // Design Menu
        JMenu designMenu = new JMenu("Design");
        JMenuItem changeBackgroundItem = createMenuItem("Change Background", KeyEvent.VK_B, null);
        JMenuItem changeTextColorItem = createMenuItem("Change Text Color", KeyEvent.VK_T, null);
        
        changeBackgroundItem.addActionListener(e -> changeBackgroundColor());
        changeTextColorItem.addActionListener(e -> changeTextColor());
        designMenu.add(changeBackgroundItem);
        designMenu.add(changeTextColorItem);
        menuBar.add(designMenu);

        //dev menu
        
        /*JMenu devMenu = new JMenu("Dev");
        JMenuItem compileItem = new JMenuItem("Compile Java Code");
        JMenuItem runItem = new JMenuItem("Run Java Code");
        
        compileItem.setAccelerator(KeyStroke.getKeyStroke("F9"));
        runItem.setAccelerator(KeyStroke.getKeyStroke("F10"));
        
        compileItem.addActionListener(e -> compileFile());
        runItem.addActionListener(e -> runFile());
        devMenu.add(compileItem);
        devMenu.add(runItem);
        menuBar.add(devMenu);*/

        setJMenuBar(menuBar);

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK), "increaseFont");
        textArea.getActionMap().put("increaseFont", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeFontSize(1);
            }
        });

        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK), "decreaseFont");
        textArea.getActionMap().put("decreaseFont", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeFontSize(-1);
            }
        });

        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Ln: 1, Col: 1, Font Size: " + defaultFontSize + "pt");
        statusBar.add(statusLabel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    public void print(String report){
        textArea.setText(textArea.getText().concat("\n"+report));
    }
    
    private JMenuItem createMenuItem(String text, int mnemonic, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(text+"         ");
        menuItem.setMnemonic(mnemonic);
        menuItem.setAccelerator(accelerator);
        menuItem.setHorizontalTextPosition(SwingConstants.LEFT);
        menuItem.setHorizontalAlignment(SwingConstants.LEFT);
        return menuItem;
    }
    
    private void compileFile() {
        if (currentFile != null) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler != null) {
                int result = compiler.run(null, null, null, currentFile.getPath());
                if (result == 0) {
                    JOptionPane.showMessageDialog(this, "Compilation successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Compilation failed.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Java Compiler not available.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file to compile.");
        }
    }
    
    private void runFile() {
        if (currentFile != null) {
            // Assuming the class file is in the same directory as the source file
            String className = currentFile.getName().replace(".java", "");
            try {
                Process process = Runtime.getRuntime().exec("java " + className);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to run the file.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file to run.");
        }
    }
    
    private void changeBackgroundColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Background Color", textArea.getBackground());
        if (newColor != null) {
            textArea.setBackground(newColor);
        }
    }

    private void changeTextColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Text Color", textArea.getForeground());
        if (newColor != null) {
            textArea.setForeground(newColor);
        }
    }

    private void changeFont() {
        JDialog fontDialog = new JDialog(this, "Change Font", true);
        fontDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel fontLabel = new JLabel("Select Font: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        fontDialog.add(fontLabel, gbc);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        JComboBox<String> fontBox = new JComboBox<>(fontNames);
        fontBox.setEditable(true);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fontDialog.add(fontBox, gbc);

        JLabel styleLabel = new JLabel("Font Style: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        fontDialog.add(styleLabel, gbc);

        String[] styles = {"Regular", "Bold", "Italic", "Bold Italic"};
        JComboBox<String> styleBox = new JComboBox<>(styles);
        styleBox.setEditable(true);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fontDialog.add(styleBox, gbc);

        JLabel sizeLabel = new JLabel("Font Size: ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        fontDialog.add(sizeLabel, gbc);

        Integer[] sizes = {8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40, 48, 56, 64, 72};
        JComboBox<Integer> sizeBox = new JComboBox<>(sizes);
        sizeBox.setEditable(true);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fontDialog.add(sizeBox, gbc);

        JLabel sampleLabel = new JLabel("Sample Text");
        sampleLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        fontDialog.add(sampleLabel, gbc);

        fontBox.addActionListener(e -> updateSampleFont(fontBox, styleBox, sizeBox, sampleLabel));
        styleBox.addActionListener(e -> updateSampleFont(fontBox, styleBox, sizeBox, sampleLabel));
        sizeBox.addActionListener(e -> updateSampleFont(fontBox, styleBox, sizeBox, sampleLabel));

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        okButton.addActionListener(e -> {
            String selectedFont = (String) fontBox.getSelectedItem();
            int selectedStyle = styleBox.getSelectedIndex();
            int selectedSize = (int) sizeBox.getSelectedItem();
            Font newFont = new Font(selectedFont, selectedStyle, selectedSize);
            textArea.setFont(newFont);
            fontDialog.dispose();
        });
        cancelButton.addActionListener(e -> fontDialog.dispose());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        fontDialog.add(buttonPanel, gbc);

        fontDialog.pack();
        fontDialog.setLocationRelativeTo(this);
        fontDialog.setVisible(true);
    }

    private void updateSampleFont(JComboBox<String> fontBox, JComboBox<String> styleBox, JComboBox<Integer> sizeBox, JLabel sampleLabel) {
        String selectedFont = (String) fontBox.getSelectedItem();
        int selectedStyle = styleBox.getSelectedIndex();
        int selectedSize = (int) sizeBox.getSelectedItem();
        Font newFont = new Font(selectedFont, selectedStyle, selectedSize);
        sampleLabel.setFont(newFont);
    }


    private void newFile() {
        textArea.setText("");
        currentFile = null;
        isModified = false;
        updateTitle();
    }

    private void newWindow() {
        SwingUtilities.invokeLater(() -> {
            Notes newEditor = new Notes();
            newEditor.setVisible(true);
        });
    }

    private void openFile() {
        boolean a = ch.showOpenDialog(this);
        if(a) {
            currentFile = ch.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                textArea.read(reader, null);
                isModified = false;
                updateTitle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void openFile(String filePath) {
        currentFile = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
            textArea.read(reader, null);
            isModified = false;
            updateTitle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                textArea.write(writer);
                isModified = false;
                updateTitle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveFileAs();
        }
    }

    private void saveFileAs() {
        boolean c = ch.showSaveDialog(this);
        if (c) {
            currentFile = ch.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                textArea.write(writer);
                isModified = false;
                updateTitle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTitle() {
        if (currentFile != null) {
            String title = currentFile.getName() + " - Notes";
            if (isModified) {
                title = "*" + title;
            }
            setTitle(title);
        } else {
            setTitle("*untitled.txt - Notes");
        }
    }

    private void changeFontSize(int delta) {
        Font currentFont = textArea.getFont();
        float newSize = currentFont.getSize() + delta;
        if (newSize > 0) {
            textArea.setFont(currentFont.deriveFont(newSize));
            updateStatusBar();
        }
    }

    private void resetFontSize() {
        textArea.setFont(font.deriveFont(defaultFontSize));
        updateStatusBar();
    }

    private void updateStatusBar() {
        try {
            int line = textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
            int column = textArea.getCaretPosition() - textArea.getLineStartOffset(textArea.getLineOfOffset(textArea.getCaretPosition())) + 1;
            float fontSize = textArea.getFont().getSize();
            String statusText = String.format("Ln: %d, Col: %d, Font Size: %.0fpt",
                    line, column, fontSize);
            statusLabel.setText(statusText);
        } catch (javax.swing.text.BadLocationException e) {
            e.printStackTrace();
        }
    }

    private boolean promptForUnsavedChanges() {
        if (isModified) {
            // Custom options for the JOptionPane
            Object[] options = {"Save", "Don't Save", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this,
                    "You have unsaved changes. Do you want to save them before exiting?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case JOptionPane.YES_OPTION:
                    saveFile();
                    return !isModified; // If save was successful and isModified is false, then proceed to exit
                case JOptionPane.NO_OPTION:
                    return true; // Proceed to exit without saving
                case JOptionPane.CANCEL_OPTION:
                default:
                    return false; // Cancel exit
            }
        }
        return true; // No unsaved changes, proceed to exit
    }

    private void showFindDialog() {
        JDialog findDialog = new JDialog(this, "Find", false);
        findDialog.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel findLabel = new JLabel("Find: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(findLabel, gbc);
        JTextField findText = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(findText, gbc);
        JCheckBox matchCase = new JCheckBox("Match Case");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(matchCase, gbc);
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton findNextButton = new JButton("Find Next");
        JButton findPrevButton = new JButton("Find Previous");
        JButton cancelButton = new JButton("Cancel");
        findNextButton.addActionListener(e -> findText(findText.getText(), matchCase.isSelected(), true));
        findPrevButton.addActionListener(e -> findText(findText.getText(), matchCase.isSelected(), false));
        cancelButton.addActionListener(e -> findDialog.dispose());
        buttonPanel.add(findNextButton);
        buttonPanel.add(findPrevButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(buttonPanel, gbc);
        findDialog.add(panel, BorderLayout.CENTER);
        findDialog.pack();
        findDialog.setLocationRelativeTo(this);
        findDialog.setVisible(true);
    }
    
    private void showReplaceDialog() {
        JDialog replaceDialog = new JDialog(this, "Replace", false);
        replaceDialog.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel findLabel = new JLabel("Find: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(findLabel, gbc);
        JTextField findText = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(findText, gbc);

        JLabel replaceLabel = new JLabel("Replace: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(replaceLabel, gbc);

        JTextField replaceText = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(replaceText, gbc);

        JCheckBox matchCase = new JCheckBox("Match Case");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(matchCase, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        JButton findNextButton = new JButton("Find Next");
        JButton findPrevButton = new JButton("Find Previous");
        JButton replaceButton = new JButton("Replace");
        JButton replaceAllButton = new JButton("Replace All");
        JButton cancelButton = new JButton("Cancel");

        findNextButton.addActionListener(e -> findText(findText.getText(), matchCase.isSelected(), true));
        findPrevButton.addActionListener(e -> findText(findText.getText(), matchCase.isSelected(), false));
        replaceButton.addActionListener(e -> replaceText(findText.getText(), replaceText.getText(), matchCase.isSelected()));
        replaceAllButton.addActionListener(e -> replaceAllText(findText.getText(), replaceText.getText(), matchCase.isSelected()));
        cancelButton.addActionListener(e -> replaceDialog.dispose());

        buttonPanel.add(findNextButton);
        buttonPanel.add(findPrevButton);
        buttonPanel.add(replaceButton);
        buttonPanel.add(replaceAllButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(buttonPanel, gbc);

        replaceDialog.add(panel, BorderLayout.CENTER);
        replaceDialog.pack();
        replaceDialog.setLocationRelativeTo(this);
        replaceDialog.setVisible(true);
    }


    private void findText(String text, boolean matchCase, boolean forward) {
        String content = textArea.getText();
        String searchText = text;
        if (!matchCase) {
            content = content.toLowerCase();
            searchText = searchText.toLowerCase();
        }
        int startPos = textArea.getCaretPosition();
        int searchPos;
        if (forward) {
            searchPos = content.indexOf(searchText, startPos);
            if (searchPos == -1) { 
                searchPos = content.indexOf(searchText, 0);
            }
        } else {
            searchPos = content.lastIndexOf(searchText, startPos - searchText.length() - 1);
            if (searchPos == -1) { 
                searchPos = content.lastIndexOf(searchText);
            }
        }
        if (searchPos != -1) {
            textArea.setCaretPosition(searchPos);
            textArea.moveCaretPosition(searchPos + searchText.length());
        } else {
            JOptionPane.showMessageDialog(this, "Text not found.");
        }
    }

    private void replaceText(String findText, String replaceText, boolean matchCase) {
        String content = textArea.getText();
        String searchText = findText;

        if (!matchCase) {
            content = content.toLowerCase();
            searchText = searchText.toLowerCase();
        }

        int startPos = textArea.getCaretPosition();
        int searchPos = content.indexOf(searchText, startPos);

        if (searchPos != -1) {
            textArea.setCaretPosition(searchPos);
            textArea.moveCaretPosition(searchPos + searchText.length());
            textArea.replaceSelection(replaceText);
        } else {
            JOptionPane.showMessageDialog(this, "Text not found.");
        }
    }

    private void replaceAllText(String findText, String replaceText, boolean matchCase) {
        String content = textArea.getText();
        String searchText = findText;

        if (!matchCase) {
            content = content.toLowerCase();
            searchText = searchText.toLowerCase();
        }

        int startPos = 0;
        int searchPos = content.indexOf(searchText, startPos);

        while (searchPos != -1) {
            textArea.setCaretPosition(searchPos);
            textArea.moveCaretPosition(searchPos + searchText.length());
            textArea.replaceSelection(replaceText);
            startPos = searchPos + replaceText.length();
            content = textArea.getText();
            searchPos = content.indexOf(searchText, startPos);
        }
    }

    private void goToLine() {
        String lineStr = JOptionPane.showInputDialog(this, "Enter line number:", "Go To Line", JOptionPane.PLAIN_MESSAGE);
        if (lineStr != null) {
            try {
                int line = Integer.parseInt(lineStr);
                int pos = textArea.getLineStartOffset(line - 1);
                textArea.setCaretPosition(pos);
            } catch (NumberFormatException | javax.swing.text.BadLocationException e) {
                JOptionPane.showMessageDialog(this, "Invalid line number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void insertTimeDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String currentTimeDate = sdf.format(new Date());
        textArea.insert("[" + currentTimeDate + "]", textArea.getCaretPosition());
    }
}
