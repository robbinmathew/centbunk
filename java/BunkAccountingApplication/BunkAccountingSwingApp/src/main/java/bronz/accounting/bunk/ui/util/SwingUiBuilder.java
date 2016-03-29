package bronz.accounting.bunk.ui.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import bronz.accounting.bunk.ui.panel.BasePanel;
import bronz.accounting.bunk.ui.panel.CalendarButton;
import bronz.utilities.general.ReflectionUtil;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.custom.LabeledComponent;
import bronz.utilities.swing.custom.ValidatableTextField;
import bronz.utilities.swing.custom.ValidatableTextField.ValidationType;

public class SwingUiBuilder
{
    public enum UiElement
    { 
        JBUTTON,
        KEY_VALUE,
        JPANEL,
        CALENDAR_BUTTON,
        JTEXTFIELD,
        JTEXTFIELD_HORIZONTAL,
        JCOMBOBOX,
        JCOMBOBOX_HORIZONTAL,
        JTABLE,
        JLABEL,
        TEXTAREA;
    }
    
    private static final int PAGE_SIDE_MARGIN = 50;
    private static final int PAGE_TOP_MARGIN = 50;
    private static final int PAGE_TOP_MARGIN_WITH_SUB_OPTIONS = 100;
    
    private final JPanel panel;
    private final Rectangle panelSize;
    private final Map<String, JComponent> componentMap;
    private JFrame parentWindow;
    
    

	public SwingUiBuilder( final JPanel panel, final Rectangle panelSize,
            final Map<String, JComponent> componentMap )
    {
        this.panel = panel;
        this.panelSize = panelSize;
        this.panel.setLayout( null );
        this.panel.setBounds( panelSize );
        this.componentMap = componentMap;
    }
    
    public SwingUiBuilder( final JPanel panel, final Rectangle panelSize )
    {
        this( panel, panelSize, new HashMap<String, JComponent>() );
    }
    
    public JComponent addElement( final UiElement element,
            final String elementId, final Object... objects  )
    {
        return addElementToPanel( this.panel, element, elementId, objects );
    }
    
    public void addFormFields( final Object... objects  )
    {
        addFormFieldsToPanel( this.panel, objects );
    }
    
    public JFrame getParentWindow()
    {
		return parentWindow;
	}

	public void setParentWindow(JFrame parentWindow)
	{
		this.parentWindow = parentWindow;
	}
    
    public void addFormFields( final String panelId, final Object... objects  )
    {
        //Get panel instance.
        final JPanel panel = ReflectionUtil.getValue( JPanel.class,
                this.componentMap.get( panelId ) );
        ValidationUtil.checkIfNull( panel, "No Panel found with id:" + panelId );
        addFormFieldsToPanel( panel, objects );
    }
    
    private void addFormFieldsToPanel( final JPanel panel,
            final Object... objects  )
    {
        final int noOfFields = objects.length / 3;
        final int fieldHeight = 20;
        final int panelWidth = panel.getWidth();
        int fieldY = 5;
        int argNum = 0;
        for ( int i = 0; i < noOfFields; i++ )
        {
            final UiElement element = ReflectionUtil.getValue( UiElement.class,
                    objects[ argNum++ ]);
            final String elementId = ReflectionUtil.getValue( String.class,
                    objects[ argNum++ ]);
            final Object validation = ReflectionUtil.getValue(
                    Object.class, objects[ argNum++ ]);
            
            if ( UiElement.JBUTTON == element )
            {
                addElementToPanel( panel, element, elementId, 100, fieldY,
                        panelWidth - 200 , fieldHeight, validation, true );
            }
            else
            {
                addElementToPanel( panel, element, elementId, 0, fieldY,
                        panelWidth, fieldHeight, validation, true );
            }
            fieldY = fieldY + 6 + fieldHeight;
        }

        panel.setPreferredSize( new Dimension( panelWidth - 10,
                (noOfFields * 26) + 10 ) );
    }

    public void addSubOptionFields( final Object... objects  )
    {
        addSubOptionFieldsToPanel( this.panel, PAGE_TOP_MARGIN, 2, objects );
    }
    
    public void addSubOptionFields( final String panelId, final Object... objects  )
    {
        //Get panel instance.
        final JPanel panel = ReflectionUtil.getValue( JPanel.class,
                this.componentMap.get( panelId ) );
        ValidationUtil.checkIfNull( panel, "No Panel found with id:" + panelId );
        addSubOptionFieldsToPanel( panel, 15, 4, objects );
    }
    
    private void addSubOptionFieldsToPanel( final JPanel panel, final int fieldY,
            final int noOfMargin, final Object... objects  )
    {
        final int noOfFields = objects.length / 4;
        final int fieldHeight = 35;
        final int panelWidth = (this.panelSize.width -
                (PAGE_SIDE_MARGIN * noOfMargin) - ((noOfFields - 1) * 10));
        final int portion = panelWidth / 100;
        int fieldX = PAGE_SIDE_MARGIN + 10;
        int argNum = 0;
        for ( int i = 0; i < noOfFields; i++ )
        {
            final UiElement element = ReflectionUtil.getValue( UiElement.class,
                    objects[ argNum++ ]);
            final String elementId = ReflectionUtil.getValue( String.class,
                    objects[ argNum++ ]);
            final Object validation = ReflectionUtil.getValue(
                    Object.class, objects[ argNum++ ]);
            final int width = ReflectionUtil.getValue( Integer.class,
                    objects[ argNum++ ] ) * portion;
            addElementToPanel( panel, element, elementId, fieldX,
                    fieldY, width, fieldHeight, validation, true );
            fieldX = fieldX + 10 + width;
        }
    }
    
    public JComponent addElementToPanel( final String panelId,
            final UiElement element, final String elementId,
            final Object... objects  )
    {
        //Get panel instance.
        final JPanel panel = ReflectionUtil.getValue( JPanel.class,
                this.componentMap.get( panelId ) );
        ValidationUtil.checkIfNull( panel, "No Panel found with id:" + panelId );
        return addElementToPanel( panel, element, elementId, objects );
    }
    
    public <T extends JComponent>void addPanelsToPanel( final String panelId,
            final List<T> subPanels, final int subPanelHieght )
    {
        //Get panel instance.
        final JPanel panel = ReflectionUtil.getValue( JPanel.class,
                this.componentMap.get( panelId ) );
        ValidationUtil.checkIfNull( panel, "No Panel found with id:" + panelId );
        panel.setLayout( null );
        final int panelInternalMargin = 20;
        int panelY = panelInternalMargin;
        int subPanelWidth = panel.getWidth() - (panelInternalMargin * 2);
        
        for( final JComponent subPanel : subPanels )
        {
            subPanel.setBounds( panelInternalMargin, panelY, subPanelWidth,
                    subPanelHieght );
            panelY = panelY + 10 + subPanelHieght;
            panel.add( subPanel );
        }
        panel.setPreferredSize( new Dimension( panel.getWidth() - 5, panelY + 10) );
    }
    
    public void addElement( final JComponent component,
            final String elementId, final Object... objects  )
    {
        this.componentMap.put( elementId, component );
        setBoundForComponent( component, objects );
        this.panel.add( component );
    }
    
    public void addDetailPanels( final Object... objects  )
    {
        addDetailPanels( PAGE_TOP_MARGIN_WITH_SUB_OPTIONS , objects );
    }
    
    public void addDetailPanelsWithoutSubOptions( final Object... objects  )
    {
        addDetailPanels( PAGE_TOP_MARGIN , objects );
    }
    
    private void addDetailPanels( int tableOffsetY, final Object... objects  )
    {
        final int noOfPanels = objects.length / 3;
        
        final int tableWidth = this.panelSize.width - (PAGE_SIDE_MARGIN * 2);
        final int tableHeight = this.panelSize.height - PAGE_SIDE_MARGIN -
            tableOffsetY;
        int argNum = 0;
        final float portion = tableHeight / 100f;
        for ( int i = 0; i < noOfPanels; i++ )
        {
            final String elementId = ReflectionUtil.getValue( String.class,
                    objects[ argNum++ ] );
            final JComponent panel = ReflectionUtil.getValue( JComponent.class,
                    objects[ argNum++ ] );
            final JScrollPane scrollPane = new JScrollPane( panel );
            panel.setName( elementId );
            
            this.panel.add( scrollPane );
            this.componentMap.put( elementId, panel );
            final Float panelHeight = ReflectionUtil.getValue( Integer.class,
                    objects[ argNum++ ] ) * portion;
            scrollPane.setBounds( PAGE_SIDE_MARGIN, tableOffsetY, tableWidth,
                    panelHeight.intValue() - 1 );
            panel.setBounds( 0, 0, tableWidth, panelHeight.intValue() - 1 );
            tableOffsetY = tableOffsetY + panelHeight.intValue();
        }
    }
    
    
    public void addDetailPanelsWithScroll( final Object... objects  )
    {
    	addDetailPanelsWithScroll(PAGE_TOP_MARGIN_WITH_SUB_OPTIONS, objects);
    }
    
    public void addDetailPanelsWithSingleScroll( final String panelName,
    		final Object... objects  )
    {
    	addDetailPanelsWithSingleScroll(PAGE_TOP_MARGIN_WITH_SUB_OPTIONS,
    			panelName, objects);
    }
    
    public void addDetailPanelsWithScrollWithoutSubOptions( final Object... objects  )
    {
    	addDetailPanelsWithScroll(PAGE_TOP_MARGIN, objects);
    }
    
    protected void addDetailPanelsWithScroll( final int topMargin, final Object... objects  )
    {
        final int noOfPanels = objects.length / 3;
        final JPanel scrollPanel = new BasePanel();
        final JScrollPane parentScrollPane = new JScrollPane( scrollPanel );
        parentScrollPane.getVerticalScrollBar().setUnitIncrement( 50 );
        final int tableWidth = this.panelSize.width - (PAGE_SIDE_MARGIN * 2);
        final int tableHeight = this.panelSize.height - PAGE_SIDE_MARGIN - topMargin;
        parentScrollPane.setBounds( PAGE_SIDE_MARGIN, topMargin, tableWidth, tableHeight );
        this.panel.add( parentScrollPane );
        int argNum = 0;
        int tableOffsetY = 10;
        int totalParentHieght = tableOffsetY;
        final float portion = tableHeight / 100f;
        for ( int i = 0; i < noOfPanels; i++ )
        {
            final String elementId = ReflectionUtil.getValue( String.class,
                    objects[ argNum++ ] );
            final JComponent panel = ReflectionUtil.getValue( JComponent.class,
                    objects[ argNum++ ] );
            final JScrollPane scrollPane = new JScrollPane( panel );
            panel.setName( elementId );
            
            scrollPanel.add( scrollPane );
            this.componentMap.put( elementId, panel );
            final Float panelHeight = ReflectionUtil.getValue( Integer.class,
                    objects[ argNum++ ] ) * portion;
            totalParentHieght = totalParentHieght + panelHeight.intValue() + 10;
            scrollPane.setBounds( 10, tableOffsetY, tableWidth - 30,
                    panelHeight.intValue() - 1 );
            panel.setBounds( 0, 0, tableWidth - 25, panelHeight.intValue() - 1 );
            tableOffsetY = tableOffsetY + panelHeight.intValue();
        }
        scrollPanel.setPreferredSize( new Dimension( tableWidth, totalParentHieght - 40 ) );
    }
    
    protected void addDetailPanelsWithSingleScroll( final int topMargin,
    		final String panelName,	final Object... objects  )
    {
        final int noOfPanels = objects.length / 2;
        //Make and add the panel wrapped in a scrollpane
        final JPanel panel = new BasePanel();
        final SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        
        final int compSpacing = 0;
        int argNum = 0;
        JComponent previousComp = null;
        for ( int i = 0; i < noOfPanels; i++ )
        {
            final String elementId = ReflectionUtil.getValue( String.class,
                    objects[ argNum++ ] );
            final JComponent comp = ReflectionUtil.getValue( JComponent.class,
                    objects[ argNum++ ] );
            comp.setName( elementId );
            this.componentMap.put( elementId, comp );
            
            if (comp instanceof JTable )
            {
            	final JTable table = (JTable) comp;
            	if ( previousComp == null)
                {
                	layout.putConstraint(SpringLayout.NORTH, table.getTableHeader(), compSpacing, SpringLayout.NORTH, panel);
                }
                else
                {
                	layout.putConstraint(SpringLayout.NORTH, table.getTableHeader(), compSpacing, SpringLayout.SOUTH, previousComp);
                }
                
                layout.putConstraint(SpringLayout.WEST, table.getTableHeader(), compSpacing, SpringLayout.WEST, panel);
                layout.putConstraint(SpringLayout.EAST, table.getTableHeader(), compSpacing, SpringLayout.EAST, panel);
                
                panel.add(table.getTableHeader());
                previousComp = table.getTableHeader();
            }
            
            if ( previousComp == null)
            {
            	layout.putConstraint(SpringLayout.NORTH, comp, compSpacing, SpringLayout.NORTH, panel);
            }
            else
            {
            	layout.putConstraint(SpringLayout.NORTH, comp, compSpacing, SpringLayout.SOUTH, previousComp);
            }
            
            layout.putConstraint(SpringLayout.WEST, comp, compSpacing, SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.EAST, comp, compSpacing, SpringLayout.EAST, panel);
            
            if ( i == (noOfPanels - 1))
            {
            	layout.putConstraint(SpringLayout.SOUTH, comp, compSpacing, SpringLayout.SOUTH, panel);
            }
            panel.add(comp);
            previousComp = comp;
        }
        
        final JScrollPane parentScrollPane = new JScrollPane( panel,
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parentScrollPane.getVerticalScrollBar().setUnitIncrement( 50 );
        final int tableWidth = this.panelSize.width - (PAGE_SIDE_MARGIN * 2);
        final int tableHeight = this.panelSize.height - PAGE_SIDE_MARGIN - topMargin;
        parentScrollPane.setBounds( PAGE_SIDE_MARGIN, topMargin, tableWidth, tableHeight );
        parentScrollPane.setPreferredSize( new Dimension( tableWidth, tableHeight ));
        this.panel.add( parentScrollPane );
        
        this.componentMap.put( panelName, panel );
    }
    public void setPreferedWidthToTables( final JTable table,
            final int totalWidth, final int... lengths )
    {
        final Float portion = totalWidth / 100f;
        for( int i = 0; i < lengths.length ; i++ )
        {
            final TableColumn column = table.getColumnModel().getColumn( i );
            column.setPreferredWidth( lengths[ i ] * portion.intValue() );
        }
    }
    
    
    private boolean checkOptionalBoolean( final Object[] objects,
            final int index )
    {
        boolean value = false;
        if ( objects.length > index )
        {
            value = ReflectionUtil.getValue( Boolean.class, objects[index]);
        }
        return value;
    }
    
    private JComponent addElementToPanel( final JPanel panel,
            final UiElement element, final String elementId,
            final Object... objects  )
    {
        JComponent component = null;
        {
            switch( element )
            {
                case JBUTTON://argument expected :- x,y,width,height,Label
                    final JButton button = new JButton();
                    button.setText( ReflectionUtil.getValue( String.class,
                            objects[4] ) );
                    component = button;
                    setBoundForComponent( component, objects );
                    break;
                case JCOMBOBOX://argument expected :- x,y,width,height,comboboxmodel,add label(boolean)
                    final ComboBoxModel comboBoxModel = ReflectionUtil.getValue(
                            ComboBoxModel.class, objects[4] );
                    final JComboBox comboBox = new JComboBox( comboBoxModel );
                    component = comboBox;
                    if ( comboBoxModel.getSize() > 0 )
                    {
                        comboBox.setSelectedIndex( 0 );
                    }
                    if ( checkOptionalBoolean( objects, 5 ))
                    {
                        component = new LabeledComponent( elementId, component);
                    }
                    setBoundForComponent( component, objects );
                    break;
                case JCOMBOBOX_HORIZONTAL://argument expected :- x,y,width,height,comboboxmodel,add label(boolean)
                    final ComboBoxModel comboBoxModel1 = ReflectionUtil.getValue(
                            ComboBoxModel.class, objects[4] );
                    final JComboBox comboBox1 = new JComboBox( comboBoxModel1 );
                    component = comboBox1;
                    if ( comboBoxModel1.getSize() > 0 )
                    {
                        comboBox1.setSelectedIndex( 0 );
                    }
                    if ( checkOptionalBoolean( objects, 5 ))
                    {
                        component = new LabeledComponent( elementId, component, false );
                    }
                    setBoundForComponent( component, objects );
                    break;
                case JTEXTFIELD://argument expected :- x,y,width,height,ValidationType,add label(boolean)
                    final ValidatableTextField text = new ValidatableTextField();
                    text.setValidationFields( elementId,
                            ReflectionUtil.getValue( ValidationType.class,
                                    objects[4] ) );
                    component = text;
                    if ( checkOptionalBoolean( objects, 5 ))
                    {
                        component = new LabeledComponent( elementId, component);
                    }
                    setBoundForComponent( component, objects );
                    break;
                case JTEXTFIELD_HORIZONTAL://argument expected :- x,y,width,height,ValidationType,add label(boolean)
                    final ValidatableTextField text1 = new ValidatableTextField();
                    text1.setValidationFields( elementId,
                            ReflectionUtil.getValue( ValidationType.class,
                                    objects[4] ) );
                    component = text1;
                    if ( checkOptionalBoolean( objects, 5 ))
                    {
                        component = new LabeledComponent( elementId, component, false );
                    }
                    setBoundForComponent( component, objects );
                    break;
                case JLABEL://argument expected :- x,y,width,height,Label, font(optional)
                    final JLabel label = new JLabel();
                    label.setText(
                        ReflectionUtil.getValue( String.class, objects[4] ) );
                    if (objects.length >5)
                    {
                        label.setFont( ReflectionUtil.getValue( Font.class, objects[5] ) );
                    }
                    component = label;
                    setBoundForComponent( component, objects );
                    break;
                case KEY_VALUE://argument expected :- x,y,labelWidth,textWidth,height,Label
                    final JLabel labelField = new JLabel();
                    labelField.setText(
                        ReflectionUtil.getValue( String.class, objects[5] ) );
                    labelField.setBounds(
                            ReflectionUtil.getValue( Integer.class, objects[0]),
                            ReflectionUtil.getValue( Integer.class, objects[1]),
                            ReflectionUtil.getValue( Integer.class, objects[2]),
                            ReflectionUtil.getValue( Integer.class, objects[4]));
                    panel.add( labelField );
                    final JTextField textField = new JTextField();
                    textField.setBounds(
                            ReflectionUtil.getValue( Integer.class, objects[0]) +
                            ReflectionUtil.getValue( Integer.class, objects[2]),
                            ReflectionUtil.getValue( Integer.class, objects[1]),
                            ReflectionUtil.getValue( Integer.class, objects[3]),
                            ReflectionUtil.getValue( Integer.class, objects[4]));
                    component = textField;
                    break;
                case CALENDAR_BUTTON://argument expected :- x,y,dateinteger,Label,Parent Frame
                    final CalendarButton calButton = new CalendarButton(
                        ReflectionUtil.getValue( Integer.class, objects[0]),
                        ReflectionUtil.getValue( Integer.class, objects[1]),
                        ReflectionUtil.getValue( JFrame.class, objects[4]),
                        ReflectionUtil.getValue( Integer.class, objects[2]),
                        ReflectionUtil.getValue( String.class, objects[3]), false);
                    component = calButton;
                    break;
                case TEXTAREA://argument expected :- x,y,width,height,requires scroll(boolean),editable(boolean)
                    final JTextPane textArea = new JTextPane();
                    if ( ReflectionUtil.getValue( Boolean.class, objects[4]) )
                    {
                        final JScrollPane scrollPane = new JScrollPane( textArea );
                        panel.add( scrollPane );
                        setBoundForComponent( scrollPane, objects );
                    }
                    else
                    {
                        setBoundForComponent( textArea, objects );
                        panel.add( textArea );
                    }
                    textArea.setEditable( ReflectionUtil.getValue( Boolean.class,
                            objects[5]) );
                    component = textArea;
                    break;
                case JTABLE://argument expected :- x,y,width,height,requires scroll(boolean),Tablemodel,content(array or vector)
                    final JTable table = new JTable( (TableModel) objects[5] );
                    if ( ReflectionUtil.getValue( Boolean.class, objects[4]) )
                    {
                        final JScrollPane scrollPane = new JScrollPane( table );
                        panel.add( scrollPane );
                        setBoundForComponent( scrollPane, objects );
                    }
                    else
                    {
                        setBoundForComponent( table, objects );
                        panel.add( table );
                    }
                    component = table;
                    break;
                case JPANEL://argument expected :- x,y,width,height,requires scroll(boolean)
                    final JPanel subPanel = new JPanel();
                    if ( ReflectionUtil.getValue( Boolean.class, objects[4]) )
                    {
                        final JScrollPane scrollPane =
                            new JScrollPane( subPanel );
                        panel.add( scrollPane );
                        setBoundForComponent( scrollPane, objects );
                    }
                    else
                    {
                        setBoundForComponent( subPanel, objects );
                        panel.add( subPanel );
                    }
                    component = subPanel;
                    break;
                default:
                    throw new IllegalStateException( "No handling for " +
                            element );
            }
            if( component != null )
            {
                this.componentMap.put( elementId, component );
                if ( UiElement.JTABLE != element &&
                        UiElement.TEXTAREA != element &&
                        UiElement.JPANEL != element )
                {
                    panel.add( component );
                }
            }
        }
        return component;
    }
    
    private void setBoundForComponent( final JComponent component,
            final Object[] objects )
    {
        component.setBounds(
                ReflectionUtil.getValue( Integer.class, objects[0]),
                ReflectionUtil.getValue( Integer.class, objects[1]),
                ReflectionUtil.getValue( Integer.class, objects[2]),
                ReflectionUtil.getValue( Integer.class, objects[3]));
    }
}
