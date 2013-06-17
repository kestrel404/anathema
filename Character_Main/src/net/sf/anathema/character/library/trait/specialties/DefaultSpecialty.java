package net.sf.anathema.character.library.trait.specialties;

import net.sf.anathema.character.generic.framework.ITraitReference;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.TraitContext;
import net.sf.anathema.character.generic.impl.traits.SimpleTraitTemplate;
import net.sf.anathema.character.generic.traits.TraitType;
import net.sf.anathema.character.library.trait.DefaultTrait;
import net.sf.anathema.character.library.trait.DefaultTraitType;
import net.sf.anathema.character.library.trait.FriendlyValueChangeChecker;
import net.sf.anathema.character.library.trait.rules.TraitRules;
import net.sf.anathema.character.library.trait.subtrait.AbstractSubTraitContainer;

public class DefaultSpecialty extends DefaultTrait implements Specialty {

  private final String subTraitName;
  private final AbstractSubTraitContainer container;
  private final ITraitReference reference;

  public DefaultSpecialty(AbstractSubTraitContainer container, ITraitReference reference, String specialtyName, TraitContext context) {
    super(new TraitRules(new DefaultTraitType("Specialty"), SimpleTraitTemplate.createStaticLimitedTemplate(0, 3), context.getLimitationContext()), context,
            new FriendlyValueChangeChecker());
    this.container = container;
    this.reference = reference;
    this.subTraitName = specialtyName;
  }

  @Override
  public String getName() {
    return subTraitName;
  }

  @Override
  public ITraitReference getTraitReference() {
    return reference;
  }

  @Override
  public TraitType getBasicTraitType() {
    return reference.getTraitType();
  }

  @Override
  public void setCurrentValue(int value) {
    int increment = value - getCurrentValue();
    if (container.getCurrentDotTotal() + increment <= SpecialtiesContainer.ALLOWED_SPECIALTY_COUNT) {
      super.setCurrentValue(value);
    } else {
      super.resetCurrentValue();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DefaultSpecialty)) {
      return false;
    }
    DefaultSpecialty other = (DefaultSpecialty) obj;
    return super.equals(obj) && other.getName().equals(getName()) && other.getBasicTraitType() == getBasicTraitType();
  }

  @Override
  public int hashCode() {
    return getName().hashCode() + getBasicTraitType().hashCode();
  }
}