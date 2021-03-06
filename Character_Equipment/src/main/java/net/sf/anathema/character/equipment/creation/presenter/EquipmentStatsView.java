package net.sf.anathema.character.equipment.creation.presenter;

import net.sf.anathema.fx.hero.configurableview.IIntegerSpinner;
import net.sf.anathema.interaction.ToggleTool;
import net.sf.anathema.lib.gui.AgnosticUIConfiguration;
import net.sf.anathema.lib.gui.selection.ObjectSelectionView;
import net.sf.anathema.lib.workflow.booleanvalue.BooleanValueView;
import net.sf.anathema.lib.workflow.textualdescription.ITextView;

public interface EquipmentStatsView {

  ITextView addLineTextView(String nameLabel);

  IIntegerSpinner addIntegerSpinner(String label);

  BooleanValueView addBooleanSelector(String label);

  ToggleTool addToggleTool();

  <T> ObjectSelectionView<T> addObjectSelection(AgnosticUIConfiguration<T> agnosticUIConfiguration);

  void addHorizontalSeparator();
}