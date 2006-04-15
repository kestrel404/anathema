package net.sf.anathema.character.generic.impl.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.disy.commons.core.util.Ensure;
import net.sf.anathema.character.generic.IBasicCharacterData;
import net.sf.anathema.character.generic.character.IGenericTraitCollection;
import net.sf.anathema.character.generic.character.IMagicCollection;
import net.sf.anathema.character.generic.impl.magic.charm.prerequisite.CompositeLearnWorker;
import net.sf.anathema.character.generic.impl.magic.charm.prerequisite.ICharmLearnWorker;
import net.sf.anathema.character.generic.impl.magic.charm.prerequisite.SelectiveCharmGroup;
import net.sf.anathema.character.generic.impl.magic.persistence.CharmCache;
import net.sf.anathema.character.generic.impl.magic.persistence.prerequisite.CharmPrerequisiteList;
import net.sf.anathema.character.generic.impl.magic.persistence.prerequisite.SelectiveCharmGroupTemplate;
import net.sf.anathema.character.generic.magic.ICharm;
import net.sf.anathema.character.generic.magic.ICharmData;
import net.sf.anathema.character.generic.magic.IMagicVisitor;
import net.sf.anathema.character.generic.magic.charms.CharmType;
import net.sf.anathema.character.generic.magic.charms.ComboRestrictions;
import net.sf.anathema.character.generic.magic.charms.Duration;
import net.sf.anathema.character.generic.magic.charms.ICharmAlternative;
import net.sf.anathema.character.generic.magic.charms.ICharmAttribute;
import net.sf.anathema.character.generic.magic.charms.ICharmAttributeRequirement;
import net.sf.anathema.character.generic.magic.charms.ICharmLearnArbitrator;
import net.sf.anathema.character.generic.magic.charms.IComboRestrictions;
import net.sf.anathema.character.generic.magic.general.ICostList;
import net.sf.anathema.character.generic.magic.general.IMagicSource;
import net.sf.anathema.character.generic.magic.general.IPermanentCostList;
import net.sf.anathema.character.generic.template.magic.FavoringTraitType;
import net.sf.anathema.character.generic.template.magic.IFavoringTraitTypeVisitor;
import net.sf.anathema.character.generic.traits.IFavorableGenericTrait;
import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.generic.traits.types.AbilityType;
import net.sf.anathema.character.generic.traits.types.AttributeType;
import net.sf.anathema.character.generic.type.CharacterType;
import net.sf.anathema.lib.util.IIdentificate;
import net.sf.anathema.lib.util.Identificate;

public class Charm extends Identificate implements ICharm {

  public static final IIdentificate NOT_ALIEN_LEARNABLE = new Identificate("NotAlienLearnable"); //$NON-NLS-1$

  protected final CharmPrerequisiteList prerequisisteList;

  private final CharacterType characterType;
  private final IComboRestrictions comboRules;
  private final Duration duration;
  private final CharmType charmType;
  private final String group;

  private final IMagicSource[] sources;
  private final ICostList temporaryCost;
  private final IPermanentCostList permanentCost;

  private final List<ICharmAlternative> alternatives = new ArrayList<ICharmAlternative>();
  private final List<ICharm> parentCharms = new ArrayList<ICharm>();
  private final List<Charm> children = new ArrayList<Charm>();
  private final List<SelectiveCharmGroup> selectiveCharmGroups = new ArrayList<SelectiveCharmGroup>();
  private final List<ICharmAttribute> charmAttributes = new ArrayList<ICharmAttribute>();
  private final List<String> favoredCasteIds = new ArrayList<String>();

  public Charm(
      CharacterType characterType,
      String id,
      String group,
      CharmPrerequisiteList prerequisiteList,
      ICostList temporaryCost,
      IPermanentCostList permanentCost,
      IComboRestrictions comboRules,
      Duration duration,
      CharmType charmType,
      IMagicSource[] sources) {
    super(id);
    Ensure.ensureNotNull("Argument must not be null.", prerequisiteList); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", characterType); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", id); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", group); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", temporaryCost); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", permanentCost); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", comboRules); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", duration); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", charmType); //$NON-NLS-1$
    Ensure.ensureNotNull("Argument must not be null.", sources); //$NON-NLS-1$
    this.characterType = characterType;
    this.group = group;
    this.prerequisisteList = prerequisiteList;
    this.temporaryCost = temporaryCost;
    this.permanentCost = permanentCost;
    this.comboRules = comboRules;
    this.duration = duration;
    this.charmType = charmType;
    this.sources = sources;
    for (SelectiveCharmGroupTemplate template : prerequisiteList.getSelectiveCharmGroups()) {
      selectiveCharmGroups.add(new SelectiveCharmGroup(template));
    }
  }

  public Charm(ICharmData charmData) {
    super(charmData.getId());
    this.characterType = charmData.getCharacterType();
    this.group = charmData.getGroupId();
    this.temporaryCost = charmData.getTemporaryCost();
    this.permanentCost = charmData.getPermanentCost();
    this.comboRules = new ComboRestrictions();
    this.duration = charmData.getDuration();
    this.charmType = charmData.getCharmType();
    this.sources = new IMagicSource[] { charmData.getSource() };
    this.prerequisisteList = new CharmPrerequisiteList(
        charmData.getPrerequisites(),
        charmData.getEssence(),
        new String[0],
        new SelectiveCharmGroupTemplate[0],
        new ICharmAttributeRequirement[0]);
    parentCharms.addAll(charmData.getParentCharms());
  }

  public void addCharmAttribute(ICharmAttribute attribute) {
    charmAttributes.add(attribute);
  }

  public CharmType getCharmType() {
    return charmType;
  }

  public CharacterType getCharacterType() {
    return characterType;
  }

  public Duration getDuration() {
    return duration;
  }

  public IPermanentCostList getPermanentCost() {
    return permanentCost;
  }

  public IGenericTrait getEssence() {
    return prerequisisteList.getEssence();
  }

  public IGenericTrait[] getPrerequisites() {
    return prerequisisteList.getPrerequisites();
  }

  public IMagicSource getSource() {
    return sources.length > 0 ? sources[0] : null;
  }

  public ICostList getTemporaryCost() {
    return temporaryCost;
  }

  public String getGroupId() {
    return group;
  }

  public IComboRestrictions getComboRules() {
    return comboRules;
  }

  public void accept(IMagicVisitor visitor) {
    visitor.visitCharm(this);
  }

  public void addAlternative(ICharmAlternative alternative) {
    alternatives.add(alternative);
  }

  public boolean isBlockedByAlternative(IMagicCollection magicCollection) {
    for (ICharmAlternative alternative : alternatives) {
      for (ICharm charm : alternative.getCharms()) {
        boolean isThis = charm.getId().equals(getId());
        if (!isThis && magicCollection.isLearned(charm)) {
          return true;
        }
      }
    }
    return false;
  }

  public Set<ICharm> getParentCharms() {
    return new HashSet<ICharm>(parentCharms);
  }

  public void extractParentCharms(Map<String, ? extends Charm> charmsById) {
    if (parentCharms.size() > 0) {
      return;
    }
    for (final String parentId : prerequisisteList.getParentIDs()) {
      Charm parentCharm = charmsById.get(parentId);
      if (parentCharm == null) {
        parentCharm = CharmCache.getInstance().searchCharm(parentId);
      }
      Ensure.ensureNotNull("Parent charm not found for id " + getId(), parentCharm); //$NON-NLS-1$
      parentCharms.add(parentCharm);
      parentCharm.addChild(this);
    }
    for (SelectiveCharmGroup charmGroup : selectiveCharmGroups) {
      charmGroup.extractCharms(charmsById, this);
    }
  }

  public void addChild(Charm child) {
    children.add(child);
  }

  public Set<ICharm> getRenderingPrerequisiteCharms() {
    Set<ICharm> prerequisiteCharms = new HashSet<ICharm>();
    prerequisiteCharms.addAll(parentCharms);
    for (SelectiveCharmGroup charmGroup : selectiveCharmGroups) {
      prerequisiteCharms.addAll(Arrays.asList(charmGroup.getAllGroupCharms()));
    }
    return prerequisiteCharms;
  }

  public Set<ICharm> getLearnPrerequisitesCharms(ICharmLearnArbitrator learnArbitrator) {
    Set<ICharm> prerequisiteCharms = new HashSet<ICharm>();
    for (ICharm charm : getParentCharms()) {
      prerequisiteCharms.addAll(charm.getLearnPrerequisitesCharms(learnArbitrator));
      prerequisiteCharms.add(charm);
    }
    for (SelectiveCharmGroup charmGroup : selectiveCharmGroups) {
      prerequisiteCharms.addAll(charmGroup.getLearnPrerequisitesCharms(learnArbitrator));
    }
    return prerequisiteCharms;
  }

  public boolean isTreeRoot() {
    return parentCharms.size() == 0 && selectiveCharmGroups.size() == 0 && getAttributeRequirements().length == 0;
  }

  public Set<ICharm> getLearnFollowUpCharms(ICharmLearnArbitrator learnArbitrator) {
    CompositeLearnWorker learnWorker = new CompositeLearnWorker(learnArbitrator);
    for (Charm child : children) {
      child.addCharmsToForget(learnWorker);
    }
    return learnWorker.getForgottenCharms();
  }

  private void addCharmsToForget(ICharmLearnWorker learnWorker) {
    if (isCharmPrerequisiteListFullfilled(learnWorker)) {
      return;
    }
    learnWorker.forget(this);
    for (Charm child : children) {
      child.addCharmsToForget(learnWorker);
    }
  }

  private boolean isCharmPrerequisiteListFullfilled(ICharmLearnArbitrator learnArbitrator) {
    for (ICharm parent : parentCharms) {
      if (!learnArbitrator.isLearned(parent)) {
        return false;
      }
    }
    for (SelectiveCharmGroup selectiveGroup : selectiveCharmGroups) {
      if (!selectiveGroup.holdsThreshold(learnArbitrator)) {
        return false;
      }
    }
    return true;
  }

  protected ICharmAttribute[] getAttributes() {
    return charmAttributes.toArray(new ICharmAttribute[0]);
  }

  public boolean hasAttribute(IIdentificate attribute) {
    return charmAttributes.contains(attribute);
  }

  public ICharmAttributeRequirement[] getAttributeRequirements() {
    return prerequisisteList.getAttributeRequirements();
  }

  public void addFavoredCasteId(String casteId) {
    favoredCasteIds.add(casteId);
  }

  public boolean isFavored(
      IBasicCharacterData basicCharacter,
      IGenericTraitCollection traitCollection,
      FavoringTraitType type) {
    boolean specialFavored = favoredCasteIds.contains(basicCharacter.getCasteType().getId());
    if (specialFavored) {
      return true;
    }
    if (getPrerequisites().length <= 0) {
      return false;
    }
    final boolean[] characterCanFavorMagicOfPrimaryType = new boolean[1];
    final ITraitType primaryTraitType = getPrerequisites()[0].getType();
    type.accept(new IFavoringTraitTypeVisitor() {
      public void visitAbilityType(FavoringTraitType visitedType) {
        characterCanFavorMagicOfPrimaryType[0] = primaryTraitType instanceof AbilityType;
      }

      public void visitAttributeType(FavoringTraitType visitedType) {
        characterCanFavorMagicOfPrimaryType[0] = primaryTraitType instanceof AttributeType;
      }
    });
    if (characterCanFavorMagicOfPrimaryType[0] == false) {
      return false;
    }
    IGenericTrait trait = traitCollection.getTrait(primaryTraitType);
    return trait instanceof IFavorableGenericTrait && ((IFavorableGenericTrait) trait).isCasteOrFavored();
  }
}