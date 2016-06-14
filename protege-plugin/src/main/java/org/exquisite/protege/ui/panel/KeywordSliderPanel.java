package org.exquisite.protege.ui.panel;

import org.exquisite.core.costestimators.OWLAxiomKeywordCostsEstimator;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.math.BigDecimal;

public class KeywordSliderPanel extends Box {

    private ManchesterOWLSyntax kw;

    private JSlider slider;

    private JLabel sliderValueLabel;

    private final int precision = 1000;

    public KeywordSliderPanel(ManchesterOWLSyntax keyword, BigDecimal probab, OWLWorkspace owlWorkspace) {
        super(BoxLayout.X_AXIS);
        kw = keyword;

        slider = createSlider(probab);
        JLabel renderedLabel = createLabel (keyword,owlWorkspace);

        add(renderedLabel);
        add(slider);
        sliderValueLabel = new JLabel(String.valueOf(convertSliderValue(slider.getValue())));
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sliderValueLabel.setText(String.valueOf(convertSliderValue(slider.getValue())));
            }
        });
        add(sliderValueLabel);
        add(Box.createHorizontalGlue());

    }

    public double convertSliderValue(int value) {
        return ((double) value) / precision;
    }

    protected int convertProbab(BigDecimal probab) {
        int probabConverted = probab.multiply(BigDecimal.valueOf(precision)).intValue();
        if (probabConverted == 0)
            throw new IllegalStateException("Error Probability is too small to display ");
        return probabConverted;
    }

    protected JSlider createSlider (BigDecimal probab) {
        JSlider slider = new JSlider(0,precision,convertProbab(probab));
        slider.setMajorTickSpacing(precision / 10);
        slider.setMinorTickSpacing(precision/20);
        slider.setBackground(Color.WHITE);
        slider.setPaintTicks(true);
        //slider.setPaintLabels(true);

        /*Enumeration elements = slider.getLabelTable().elements();
        while(elements.hasMoreElements()) {
            JLabel label = (JLabel) elements.nextElement();
            label.setText(label.getText().substring(0,label.getText().length()-1) + "%");
        }*/

        return slider;
    }

    protected JLabel createLabel(ManchesterOWLSyntax keyword, OWLWorkspace owlWorkspace) {

        JLabel renderedLabel = new JLabel();
        Font keywordfont = new Font("Monospaced", Font.BOLD, 16); // owlWorkspace.getFont().getName()
        Color color = owlWorkspace.getKeyWordColorMap().get(keyword.toString());

        renderedLabel.setFont(keywordfont);
        if (color == null)
            color = Color.BLACK;

        int maxLength = OWLAxiomKeywordCostsEstimator.getMaxLengthKeyword();
        int diff = maxLength - keyword.toString().length();
        StringBuilder sb = new StringBuilder(keyword.toString());
        for (int i = 0; i < diff; i++)
            sb.append(" ");

        renderedLabel.setText(sb.toString());
        renderedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        renderedLabel.setForeground(color);

        return renderedLabel;
    }

    public ManchesterOWLSyntax getKeyword() {
        return kw;
    }


    public BigDecimal getProbab() {
        return BigDecimal.valueOf(slider.getValue()).divide(BigDecimal.valueOf(precision));
    }

}
