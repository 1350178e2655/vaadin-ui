package com.partior.client.ui.components;

import com.partior.client.ui.util.FontSize;
import com.partior.client.ui.util.FontWeight;
import com.partior.client.ui.util.LumoStyles;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.ui.util.css.BorderRadius;

public class Initials extends FlexBoxLayout {

	private String CLASS_NAME = "initials";

	public Initials(String initials) {
		setAlignItems(Alignment.CENTER);
		setBackgroundColor(LumoStyles.Color.Contrast._10);
		setBorderRadius(BorderRadius.L);
		setClassName(CLASS_NAME);
		UIUtils.setFontSize(FontSize.S, this);
		UIUtils.setFontWeight(FontWeight._600, this);
		setHeight(LumoStyles.Size.M);
		setJustifyContentMode(JustifyContentMode.CENTER);
		setWidth(LumoStyles.Size.M);

		add(initials);
	}

}
