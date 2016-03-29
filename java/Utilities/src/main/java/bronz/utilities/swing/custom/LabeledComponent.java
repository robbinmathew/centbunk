package bronz.utilities.swing.custom;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LabeledComponent extends JPanel
{
    private static final long serialVersionUID = 1L;
    private static final int LABEL_HIEGHT = 10;
    private final JLabel label;
    private final JComponent component;
    private final boolean isVertical;
    
    public LabeledComponent( final String labelText, final JComponent component )
    {
        this( labelText, component, true );
    }
    
    public LabeledComponent( final String labelText, final JComponent component,
            final boolean isVertical )
    {
        super();
        this.setLayout( null );
        this.isVertical = isVertical;
        this.setVisible( true );
        this.setOpaque( false );
        this.label = new JLabel( labelText );
        this.component = component;
        this.add( this.label );
        this.add( this.component );
    }
    
    public void setBounds( final int x, final int y, final int width,
            final int height)
    {
        if ( isVertical )
        {
            if ( null != this.label )
            {
                this.label.setHorizontalAlignment( SwingConstants.CENTER );
                this.label.setBounds( 0, 0, width, LABEL_HIEGHT );
            }
            if ( null != this.component )
            {
                this.component.setBounds( 0, LABEL_HIEGHT + 2, width,
                        height - LABEL_HIEGHT - 2 );
            }
        }
        else
        {
            final int componentWidth = width/3;
            if ( null != this.label )
            {
                this.label.setHorizontalAlignment( SwingConstants.RIGHT );
                this.label.setBounds( 0, 0, componentWidth , height );
            }
            if ( null != this.component )
            {
                this.component.setBounds( componentWidth + 10, 0,
                        componentWidth,  height );
            }
        }
        super.setBounds( x, y, width, height );
    }
    
    public JComponent getOriginalComp()
    {
        return this.component;
    }
    
    

}
