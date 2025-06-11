import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.util.List;

public class DeleteDialog extends JDialog {
    private JTable table;
    private JComboBox<Integer> pageSizeComboBox;
    private JButton prevButton, nextButton;
    private JTextField pageInputField;
    private JLabel currentPageLabel;
    private int currentPage = 1;
    private int totalPages = 100;
    private AlgorithmPerformanceService service;

    public DeleteDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        setSize(600, 500);

        setLocationRelativeTo(parent);

        service = new AlgorithmPerformanceService();
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        
        // 创建分页组件
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // 每页显示数量下拉框
        pageSizeComboBox = new JComboBox<>(new Integer[]{5, 10, 20, 50});
        pageSizeComboBox.addActionListener(e -> updateTableData());
        
        // 上一页/下一页按钮
        prevButton = new JButton("<");
        prevButton.addActionListener(e -> {
            if(currentPage > 1) {
                currentPage--;
                updateTableData();
            }
        });
        
        currentPageLabel = new JLabel();
        
        nextButton = new JButton(">");
        nextButton.addActionListener(e -> {
            if(currentPage < totalPages) {
                currentPage++;
                updateTableData();
            }
        });
        
        // 页码输入框
        pageInputField = new JTextField(5);
        ((AbstractDocument)pageInputField.getDocument()).setDocumentFilter(new NumberFilter());
        pageInputField.addActionListener(e -> {
            try {
                int page = Integer.parseInt(pageInputField.getText());
                if(page >= 1 && page <= totalPages) {
                    currentPage = page;
                    updateTableData();
                }
            } catch(NumberFormatException ex) {
                // 忽略无效输入
            }
        });
        
        paginationPanel.add(new JLabel("每页显示:"));
        paginationPanel.add(pageSizeComboBox);
        paginationPanel.add(prevButton);
        paginationPanel.add(currentPageLabel);
        paginationPanel.add(nextButton);
        paginationPanel.add(new JLabel("跳转到:"));
        paginationPanel.add(pageInputField);
        
        // 添加组件到对话框
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
        updateTableData();
    }
    
    private void updateTableData() {
        // 获取当前页码和每页行数
        int limit = Integer.parseInt(pageSizeComboBox.getSelectedItem().toString());
        int offset = (currentPage - 1) * limit;

        int totalRecords = service.getAllData().size();
        totalPages = totalRecords / limit + 1;

        // 调用AlgorithmPerformanceService获取分页数据

        List<AlgorithmPerformanceData> pageData = service.getDataByOffset(offset, limit);
        
        // 更新表格数据
        Object[][] tableData = new Object[pageData.size()][6];
        for(int i = 0; i < pageData.size(); i++) {
            AlgorithmPerformanceData data = pageData.get(i);
            tableData[i][0] = data.getAlgorithmName();
            tableData[i][1] = data.getPlanTime();
            tableData[i][2] = data.getMapSize().getX();
            tableData[i][3] = data.getMapSize().getY();
            tableData[i][4] = data.getObstacleDensity();
            tableData[i][5] = data.getStepsWithTimes();
        }
        
        String[] columnNames = {"算法名称", "平均规划时间(us)", "地图长度", "地图宽度", "障碍物密度", "起点、终点距离差及规划时间(us)"};
        table.setModel(new DefaultTableModel(tableData, columnNames));

        currentPageLabel.setText(currentPage + "/" + totalPages);
    }
    
    // 数字输入过滤器
    class NumberFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if(string.matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if(text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}