package net.sf.anathema.character.main.item;

import net.sf.anathema.character.generic.framework.xml.essence.GenericEssenceTemplate;
import net.sf.anathema.character.generic.traits.types.OtherTraitType;
import net.sf.anathema.character.generic.traits.types.VirtueType;
import net.sf.anathema.character.impl.generic.GenericCharacter;
import net.sf.anathema.character.main.essencepool.model.EssencePoolModelImpl;
import net.sf.anathema.character.main.testing.dummy.DummyCharacterModelContext;
import net.sf.anathema.character.main.testing.dummy.template.DummyHeroTemplate;
import net.sf.anathema.character.main.testing.dummy.trait.DummyTrait;
import net.sf.anathema.character.main.traits.model.DefaultTraitMap;
import net.sf.anathema.character.model.ICharacter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenericCharacterTest {
  ICharacter statistics = mock(ICharacter.class);

  @Before
  public void createEssenceUserWithoutPeripheralPool() throws Exception {
    DummyHeroTemplate heroTemplate = new DummyHeroTemplate();
    GenericEssenceTemplate essenceTemplate = new GenericEssenceTemplate();
    essenceTemplate.setEssenceUser(true);
    heroTemplate.setEssenceTemplate(essenceTemplate);
    when(statistics.getHeroTemplate()).thenReturn(heroTemplate);
    DefaultTraitMap traitMap = new DefaultTraitMap();
    traitMap.addTraits(new DummyTrait(OtherTraitType.Willpower));
    traitMap.addTraits(new DummyTrait(OtherTraitType.Essence));
    traitMap.addTraits(new DummyTrait(VirtueType.Compassion));
    traitMap.addTraits(new DummyTrait(VirtueType.Conviction));
    traitMap.addTraits(new DummyTrait(VirtueType.Temperance));
    traitMap.addTraits(new DummyTrait(VirtueType.Valor));
    when(statistics.getEssencePool()).thenReturn(new EssencePoolModelImpl(traitMap, heroTemplate, new DummyCharacterModelContext()));
  }

  @Test
  public void returnsNullWhenEssenceUserHasNoPeripheralPool() throws Exception {
    GenericCharacter character = new GenericCharacter(statistics);
    String peripheralPool = character.getPeripheralPool();
    assertThat(peripheralPool, is(nullValue()));
  }
}