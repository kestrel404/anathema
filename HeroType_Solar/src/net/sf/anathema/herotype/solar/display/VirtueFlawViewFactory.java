package net.sf.anathema.herotype.solar.display;

import net.sf.anathema.character.main.type.ICharacterType;
import net.sf.anathema.character.main.library.intvalue.IntValueDisplayFactoryPrototype;
import net.sf.anathema.character.main.library.virtueflaw.presenter.IDescriptiveVirtueFlawView;
import net.sf.anathema.character.main.library.virtueflaw.view.DescriptiveVirtueFlawView;
import net.sf.anathema.character.main.framework.RegisteredCharacterView;
import net.sf.anathema.character.main.view.SubViewFactory;
import net.sf.anathema.framework.value.IntegerViewFactory;

@RegisteredCharacterView(IDescriptiveVirtueFlawView.class)
public class VirtueFlawViewFactory implements SubViewFactory {
  @Override
  public <T> T create(ICharacterType type) {
    IntegerViewFactory viewFactory = IntValueDisplayFactoryPrototype.createWithMarkerForCharacterType(type);
    return (T) new DescriptiveVirtueFlawView(viewFactory);
  }
}