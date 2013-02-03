package net.sf.anathema.character.generic.impl.magic.charm.special;

import net.sf.anathema.lib.util.Identifier;
import net.sf.anathema.lib.util.Identified;
import org.junit.Test;

import static net.sf.anathema.character.generic.impl.magic.charm.special.Element.Fire;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ElementTest {

  @Test
  public void matchesIdentificateWithSameName() throws Exception {
    Identified identificate = new Identifier(Fire.name());
    assertThat(Fire.matches(identificate), is(true));
  }

  @Test
  public void doesNotMatchDifferentIdentificate() throws Exception {
    Identified identificate = new Identifier("Dawn");
    assertThat(Fire.matches(identificate), is(false));
  }
}
