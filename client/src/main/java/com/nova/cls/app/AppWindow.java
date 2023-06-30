package com.nova.cls.app;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.nova.cls.data.models.Good;
import com.nova.cls.data.models.Group;
import com.nova.cls.data.models.User;
import com.nova.cls.exceptions.ClientFailureException;
import com.nova.cls.exceptions.RequestFailureException;
import com.nova.cls.network.GoodsClient;
import com.nova.cls.network.GroupsClient;
import com.nova.cls.network.HttpCode;
import com.nova.cls.network.LoginClient;
import com.nova.cls.network.Session;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.http.HttpClient;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AppWindow extends JFrame {
    public static final String WINDOW_TITLE = "CLeanSlate Warehouse";
    public static final Dimension MIN_WINDOW_SIZE = new Dimension(1180, 820);
    private static final int STARTUP_EXTENDED_STATE = JFrame.NORMAL;

    static {
        FlatMacDarkLaf.registerCustomDefaultsSource("style");
        FlatMacDarkLaf.setup();
    }

    private final DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
    private final LoginClient loginClient;
    private final GroupsClient groupsClient;
    private final GoodsClient goodsClient;
    private final AppState state;
    JButton addGroupButton = new JButton();
    JButton addGoodButton = new JButton();
    //    List<Item> prodCopy; // TODO remove
    CardLayout cardLayout = new CardLayout();
    private JButton warehouseButton;
    private JButton statisticsButton;
    private JButton logoutButton;
    private JPanel content;
    private JPanel sidePanel;
    private JPanel groupsPage;
    private JPanel mainPanel;
    private JPanel createGroupPage;
    private JLabel createGroupNameLabel;
    private JTextField createGroupNameField;
    private JLabel createGroupDescLabel;
    private JTextArea createGroupDescriptionField;
    private JButton createGroupButton;
    private JPanel editGroupPage;
    private JTextField editGroupNameField;
    private JLabel editGroupNameLabel;
    private JLabel editGroupDescLabel;
    private JTextArea editGroupDescriptionField;
    private JButton updateGroupButton;
    private JPanel goodsPage;
    private JPanel createGoodPage;
    private JTextField createGoodNameField;
    private JTextField createGoodManufacturerField;
    private JSpinner createGoodPriceField;
    private JTextArea createGoodDescriptionField;
    private JButton createGoodButton;
    private JPanel editGoodPage;
    private JButton updateGoodButton;
    private JTextField editGoodNameField;
    private JTextField editGoodManufacturerField;
    private JSpinner editGoodPriceField;
    private JTextArea editGoodDescriptionField;
    private JPanel manageGoodPage;
    private JButton subtractQuantityButton;
    private JButton addQuantityButton;
    private JSpinner manageGoodQuantityField;
    private JLabel manageGoodNameLabel;
    private JPanel searchPage;
    private JTextField searchField;
    private JButton searchButton;
    private JScrollPane goodsScroll;
    private JScrollPane groupsScroll;
    private JLabel goodGroupLabel;
    private JLabel createGoodGroupLabel;
    private JLabel editGoodGroupLabel;
    private JLabel createGoodNameLabel;
    private JLabel createGoodManufacturerLabel;
    private JLabel createGoodPriceLabel;
    private JLabel createGoodDescLabel;
    private JLabel editGoodNameLabel;
    private JLabel editGoodManufacturerLabel;
    private JLabel editGoodPriceLabel;
    private JLabel editGoodDescLabel;
    private JButton backButton;
    private boolean isSearch;

    public AppWindow(LoginClient loginClient, Session session) {
        this.loginClient = loginClient;
        this.groupsClient = new GroupsClient(loginClient, session);
        this.goodsClient = new GoodsClient(loginClient, session);
        this.state = new AppState(session);

        createGroupDescriptionField.setLineWrap(true);
        createGroupDescriptionField.setWrapStyleWord(true);

        addGroupButton.setText("Add");
        addGroupButton.setPreferredSize(new Dimension(100, 35));
        addGoodButton.setText("Add");
        addGoodButton.setPreferredSize(new Dimension(100, 35));

        content.setLayout(cardLayout);

        content.add(groupsScroll, "groups");
        content.add(createGroupPage, "addGroup");
        content.add(editGroupPage, "editGroup");
        content.add(goodsScroll, "goods");
        content.add(editGoodPage, "editGood");
        content.add(createGoodPage, "addGood");
        content.add(manageGoodPage, "manageGood");
        content.add(searchPage, "statistics");

        logoutButton.addActionListener(e -> logout());

        state.getBreadcrumbs().visit(() -> cardLayout.show(content, "statistics"));

        warehouseButton.addActionListener(e -> {
            state.getBreadcrumbs().reset();
            state.getBreadcrumbs().visit(() -> {
                cardLayout.show(content, "groups");
                groupsView();
            });
        });

        statisticsButton.addActionListener(e -> state.getBreadcrumbs().visit(() -> {
            state.getBreadcrumbs().reset();
            cardLayout.show(content, "statistics");
        }));

        addGroupButton.addActionListener(e -> state.getBreadcrumbs().visit(() -> {
            createGroupNameField.setText("");
            createGroupDescriptionField.setText("");
            cardLayout.show(content, "addGroup");
        }));

        addGoodButton.addActionListener(e -> state.getBreadcrumbs().visit(() -> {
            Group group;
            try {
                group = groupsClient.findOne(state.getCurrentGroupId());
            } catch (Exception exception) {
                reactToUsageException(exception);
                return;
            }
            createGoodGroupLabel.setText("Group " + group.getGroupName());
            createGoodNameField.setText("");
            createGoodDescriptionField.setText("");
            createGoodManufacturerField.setText("");
            createGoodPriceField.setValue(0L);
            cardLayout.show(content, "addGood");
        }));

        createGroupButton.addActionListener(e -> {
            String name = createGroupNameField.getText();
            String description = createGroupDescriptionField.getText();
            try {
                groupsClient.create(new Group(name, description));
                state.getBreadcrumbs().back();
            } catch (Exception exception) {
                reactToUsageException(exception);
            }
        });

        updateGroupButton.addActionListener(e -> {
            String newName = editGroupNameField.getText();
            String newDescription = editGroupDescriptionField.getText();

            try {
                Group group = new Group(state.getCurrentGroupId(), newName, newDescription);
                groupsClient.update(group);
            } catch (Exception exception) {
                reactToUsageException(exception);
            }
        });

        createGoodButton.addActionListener(e -> {
            String name = createGoodNameField.getText();
            String manufacturer = createGoodManufacturerField.getText();
            String description = createGoodDescriptionField.getText();
            Long price = (long) (int) createGoodPriceField.getValue();
            try {
                Good good = new Good(name, description, manufacturer, price, state.getCurrentGroupId());
                goodsClient.create(good);
                state.getBreadcrumbs().back();
            } catch (Exception exception) {
                reactToUsageException(exception);
            }
        });

        updateGoodButton.addActionListener(e -> {
            String name = editGoodNameField.getText();
            String manufacturer = editGoodManufacturerField.getText();
            String description = editGoodDescriptionField.getText();
            Long price = (long) (int) editGoodPriceField.getValue();
            Good good = new Good(state.getCurrentGoodId(), name, description, manufacturer, price);
            try {
                goodsClient.update(good);
            } catch (Exception exception) {
                reactToUsageException(exception);
            }
        });

        subtractQuantityButton.addActionListener(e -> {
            try {
                long quantity = (long) (int) manageGoodQuantityField.getValue();
                goodsClient.offsetQuantity(state.getCurrentGoodId(), -quantity);
                Good good = goodsClient.findOne(state.getCurrentGoodId());
                manageGoodNameLabel.setText("  " + good.getGoodName() + " - " + good.getQuantity());
                manageGoodPage.revalidate();
                manageGoodPage.repaint();
            } catch (Exception exception) {
                reactToUsageException(exception);
            }
        });

        addQuantityButton.addActionListener(e -> {
            try {
                long quantity = (long) (int) manageGoodQuantityField.getValue();
                goodsClient.offsetQuantity(state.getCurrentGoodId(), quantity);
                Good good = goodsClient.findOne(state.getCurrentGoodId());
                manageGoodNameLabel.setText(good.getGoodName() + " - " + good.getQuantity());
                manageGoodPage.revalidate();
                manageGoodPage.repaint();
            } catch (Exception exception) {
                reactToUsageException(exception);
            }
        });

        searchButton.addActionListener(e -> {
            // TODO
        });

        backButton.addActionListener(e -> {
            if (state.getBreadcrumbs().atRoot()) {
                return;
            }
            state.getBreadcrumbs().back();
        });

        setContentPane(mainPanel);
        setExtendedState(STARTUP_EXTENDED_STATE);
        setMinimumSize(MIN_WINDOW_SIZE);
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new AppWindow(new LoginClient(HttpClient.newHttpClient(), new Encryptor(), new Decryptor()),
            new Session(
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcnR5bm92YSIsImV4cCI6MTY4ODY2MDgwNn0.ejEswxjTsgkN2t-3Q8dnMCm9TijIyV7XVZLQtmo8iOkUWrTKfjmJyakeLf2Rkbj5GT0Id0-nFdlDPB3PvY5yaw",
                new User(1L, "artynova", "password"))).setVisible(true);
    }

    private void reactToUsageException(Exception e) {
        if (e instanceof ClientFailureException) {
            JOptionPane.showMessageDialog(this,
                "Client has failed, we apologize for the inconvenience.\nPlease kindly present the following error to an administrator:\n"
                    + e.getMessage(),
                "Client error",
                JOptionPane.ERROR_MESSAGE);
        } else if (e instanceof RequestFailureException requestFailureException) {
            if (requestFailureException.getCode() == HttpCode.FORBIDDEN) {
                logout(false);
                return;
            } else if (requestFailureException.getCode() < HttpCode.INTERNAL_SERVER_ERROR) {
                JOptionPane.showMessageDialog(this,
                    "Could not execute your request:\n" + e.getMessage(),
                    "Invalid request",
                    JOptionPane.WARNING_MESSAGE);
                return;
            } else {
                JOptionPane.showMessageDialog(this,
                    "Server has failed, we apologize for the inconvenience.\nPlease kindly present the following error to an administrator:\n"
                        + e.getMessage(),
                    "Server error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Unrecognized error occurred, we apologize for the inconvenience.\nPlease kindly present the following error to an administrator:\n"
                    + e.getMessage(),
                "??? error",
                JOptionPane.ERROR_MESSAGE);
        }
        // only two scenarios that don't get here is
        // session end (app window still dies but gracefully)
        // and bad request (continue with this session in the same window after a popup)
        dispose();
        System.exit(1);
    }

    private void logout() {
        logout(true);
    }

    private void logout(boolean voluntary) {
        if (voluntary) {
            JOptionPane.showMessageDialog(this,
                "Goodbye, " + state.getSession().getUser().getLogin() + "!",
                "Logout successful",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Goodbye, " + state.getSession().getUser().getLogin() + "! Your session has been ended.",
                "Session over",
                JOptionPane.WARNING_MESSAGE);
        }
        dispose();
        new LoginDialog(loginClient).setVisible(true);
    }

    private void groupsView() {
        groupsPage.removeAll();
        groupsPage.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.insets = new Insets(10, 0, 0, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 3;
        JLabel label = new JLabel("Groups", SwingConstants.CENTER);
        label.setFont(new Font("Calibri", Font.PLAIN, 20));
        groupsPage.add(label, gc);
        gc.gridwidth = 1;

        List<Group> groups;
        try {
            groups = groupsClient.findAll();
        } catch (Exception e) {
            reactToUsageException(e);
            return;
        }

        for (int i = 0; i < groups.size(); i++) {
            gc.weightx = 0.5;
            gc.weighty = 0.5;
            Group currGroup = groups.get(i);
            JButton groupButton = new JButton(currGroup.getGroupName());
            groupButton.setPreferredSize(new Dimension(-1, 35));
            JButton deleteButton = new JButton("Delete");
            deleteButton.setPreferredSize(new Dimension(-1, 35));
            JButton editButton = new JButton("Edit");
            editButton.setPreferredSize(new Dimension(-1, 35));
            Long id = currGroup.getGroupId();

            groupButton.addActionListener(e -> state.getBreadcrumbs().visit(() -> {
                state.setCurrentGroupId(currGroup.getGroupId());
                List<Good> goods;
                try {
                    goods = goodsClient.findAll(Map.of("groupId", String.valueOf(currGroup.getGroupId())));
                } catch (Exception exception) {
                    reactToUsageException(exception);
                    return;
                }
                goodsView(goods, false);
                cardLayout.show(content, "goods");
            }));

            deleteButton.addActionListener(e -> {
                try {
                    groupsClient.delete(state.getCurrentGoodId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                state.getBreadcrumbs().refresh();
                groupsPage.revalidate();
                groupsPage.repaint();
            });

            editButton.addActionListener(e -> state.getBreadcrumbs().visit(() -> {
                Group group;
                try {
                    group = groupsClient.findOne(id); // group may have changed by the time button was pressed
                } catch (Exception exception) {
                    reactToUsageException(exception);
                    return;
                }
                state.setCurrentGroupId(id);
                editGroupNameField.setText(group.getGroupName());
                editGroupDescriptionField.setText(group.getDescription());
                cardLayout.show(content, "editGroup");
            }));

            gc.weightx = 3;
            gc.gridx = 0;
            gc.gridy = 2 * i + 1;
            groupsPage.add(groupButton, gc);
            gc.weightx = 0.5;

            gc.gridx = 1;
            gc.gridy = 2 * i + 1;
            groupsPage.add(deleteButton, gc);

            gc.gridx = 2;
            gc.gridy = 2 * i + 1;
            groupsPage.add(editButton, gc);

            gc.gridx = 0;
            gc.gridy = i + 1 + 1;
            gc.gridwidth = 1;
        }


        gc.gridwidth = 3;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 1;
        gc.gridx = 0;
        gc.gridy = 2 * groups.size() + 1;
        groupsPage.add(addGroupButton, gc);

        JPanel filler = new JPanel();
        filler.setOpaque(false);
        for (int j = 2 * groups.size() + 1; j <= 2 * groups.size() + 100; j++) {
            gc.gridy = j;
            gc.weightx = 1;
            gc.weighty = 10;
            groupsPage.add(filler, gc);
        }

    }

    private void goodsView(List<Good> goods, boolean search) {
        if (!search) {
            isSearch = false;
        }
        goodsPage.removeAll();
        GridBagLayout layout = new GridBagLayout();
        goodsPage.setLayout(layout);
        GridBagConstraints gc = new GridBagConstraints();

        gc.insets = new Insets(10, 0, 0, 0);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.weightx = 0.5;
        gc.weighty = 0.1;
        gc.anchor = GridBagConstraints.WEST;
        JLabel label;
        if (search) {
            // TODO search
            label = new JLabel("\"" + searchField.getText() + "\" search results", SwingConstants.CENTER);
        } else {
            Group group;
            try {
                group = groupsClient.findOne(state.getCurrentGroupId());
            } catch (Exception exception) {
                reactToUsageException(exception);
                return;
            }
            label = new JLabel("Goods from group " + group.getGroupName(), SwingConstants.CENTER);
        }
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 3;
        gc.anchor = GridBagConstraints.CENTER;

        label.setFont(new Font("Calibri", Font.PLAIN, 20));
        goodsPage.add(label, gc);
        gc.gridwidth = 1;

        for (int i = 0; i < goods.size(); i++) {
            Good currGood = goods.get(i);
            JButton goodButton = new JButton(
                currGood.getGoodName() + ", Price: " + toUAHString(currGood.getPrice()) + " UAH, In warehouse: "
                    + currGood.getQuantity() + ", Total price: " + toUAHString(currGood.getGoodId()));
            goodButton.setPreferredSize(new Dimension(-1, 35));
            goodButton.setMinimumSize(new Dimension(-1, 35));
            JButton deleteButton = new JButton("Delete");
            deleteButton.setPreferredSize(new Dimension(-1, 35));
            deleteButton.setMinimumSize(new Dimension(-1, 35));
            JButton editButton = new JButton("Edit");
            editButton.setPreferredSize(new Dimension(-1, 35));
            editButton.setMinimumSize(new Dimension(-1, 35));
            Long id = currGood.getGoodId();


            goodButton.addActionListener(e -> state.getBreadcrumbs().visit(() -> {
                Good good;
                try {
                    good = goodsClient.findOne(id); // original values may no longer be valid by the time user presses
                } catch (Exception exception) {
                    reactToUsageException(exception);
                    return;
                }
                state.setCurrentGoodId(id);
                manageGoodNameLabel.setText(good.getGoodName() + " - " + good.getQuantity());
                manageGoodNameLabel.setFont(new Font("Calibri", Font.PLAIN, 20));
                cardLayout.show(content, "manageGood");
            }));

            deleteButton.addActionListener(e -> {
                try {
                    goodsClient.delete(id);
                } catch (Exception exception) {
                    reactToUsageException(exception);
                    return;
                }
                state.getBreadcrumbs().refresh();
                goodsScroll.revalidate();
                goodsScroll.repaint();
            });


            editButton.addActionListener(e -> state.getBreadcrumbs().visit(() -> {
                Group group;
                Good good;
                try {
                    group = groupsClient.findOne(state.getCurrentGroupId());
                    good = goodsClient.findOne(id); // original values may no longer be valid by the time user presses
                } catch (Exception exception) {
                    reactToUsageException(exception);
                    return;
                }
                editGoodGroupLabel.setText(group.getGroupName());
                editGoodNameField.setText(good.getGoodName());
                editGoodDescriptionField.setText(good.getDescription());
                editGoodManufacturerField.setText(good.getManufacturer());
                editGoodPriceField.setValue(good.getPrice());
                cardLayout.show(content, "editGood");
            }));

            gc.weighty = 0.5;
            gc.weightx = 3;
            gc.gridx = 0;
            gc.gridy = i + 2;
            goodsPage.add(goodButton, gc);
            gc.weightx = 0.5;

            gc.gridx = 1;
            gc.gridy = i + 2;
            goodsPage.add(deleteButton, gc);

            gc.gridx = 2;
            gc.gridy = i + 2;
            goodsPage.add(editButton, gc);

            gc.gridx = 0;
            gc.gridy = i + 2 + 1;
            gc.gridwidth = 1;
        }

        gc.gridwidth = 3;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 1;
        gc.gridx = 0;
        gc.gridy = 2 * goods.size() + 1;
        if (!isSearch) {
            goodsPage.add(addGoodButton, gc);

        }
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        for (int j = 2 * goods.size() + 1; j <= 2 * goods.size() + 100; j++) {
            gc.gridy = j;
            gc.weightx = 1;
            gc.weighty = 10;
            goodsPage.add(filler, gc);
        }
    }

    private String toUAHString(Long kop) {
        return df.format(kop / 100.);
    }

    private void createUIComponents() {
        editGoodGroupLabel = new JLabel("", SwingConstants.CENTER);
        editGoodGroupLabel.setFont(new Font("Calibri", Font.PLAIN, 20));
        createGoodGroupLabel = new JLabel("", SwingConstants.CENTER);
        createGoodGroupLabel.setFont(new Font("Calibri", Font.PLAIN, 20));
        goodGroupLabel = new JLabel("", SwingConstants.CENTER);
        groupsScroll = new JScrollPane(groupsPage);
        goodsScroll = new JScrollPane(goodsPage);
        createGroupButton = new JButton();
        updateGroupButton = new JButton();
        createGoodButton = new JButton();
        updateGoodButton = new JButton();
        searchButton = new JButton();
        manageGoodQuantityField = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        JFormattedTextField txt = ((JSpinner.NumberEditor) manageGoodQuantityField.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        subtractQuantityButton = new JButton();
        addQuantityButton = new JButton();
    }
}



