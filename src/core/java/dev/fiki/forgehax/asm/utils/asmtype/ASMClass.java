package dev.fiki.forgehax.asm.utils.asmtype;

import dev.fiki.forgehax.api.mapper.ClassMapping;
import dev.fiki.forgehax.api.mapper.MappedFormat;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ASMClass implements Formattable<ASMClass> {
  public static ASMClass unmap(@NonNull ClassMapping mapping) {
    return ASMClass.auto(mapping._name(), mapping._obfName());
  }

  public static ASMClass single(String name) {
    return new Single(name);
  }

  public static ASMClass multi(String name, String obfName) {
    return new Container(name, obfName);
  }

  public static ASMClass auto(String name, String obfName) {
    name = Util.emptyToNull(name);
    obfName = Util.emptyToNull(obfName);
    if (obfName == null) {
      return single(name);
    } else {
      return multi(name, obfName);
    }
  }

  public abstract String getName();

  @Override
  public String toString() {
    return getName();
  }

  @Getter
  @RequiredArgsConstructor
  static private class Single extends ASMClass {
    private final String name;
  }

  @Getter
  @RequiredArgsConstructor
  static private class Multi extends ASMClass {
    private final ASMClass parent;
    private final String name;
    private final MappedFormat format;

    @Override
    public ASMClass format(MappedFormat format) {
      return getParent().format(format);
    }

    @Override
    public Stream<ASMClass> stream() {
      return getParent().stream();
    }
  }

  static private class Container extends ASMClass {
    private final ASMClass mapped;
    private final ASMClass obfuscated;

    @Getter
    @Setter
    private MappedFormat format = MappedFormat.MAPPED;

    public Container(@NonNull String className, @NonNull String obfuscatedClassName) {
      this.mapped = new Multi(this, className, MappedFormat.MAPPED);
      this.obfuscated = new Multi(this, obfuscatedClassName, MappedFormat.OBFUSCATED);
    }

    @Override
    public String getName() {
      return format(getFormat()).getName();
    }

    @Override
    public ASMClass format(MappedFormat format) {
      switch (format) {
        case MAPPED:
        case SRG:
          return this.mapped;
        case OBFUSCATED:
          return this.obfuscated;
      }
      throw new IllegalStateException();
    }

    @Override
    public Stream<ASMClass> stream() {
      return Stream.of(mapped, obfuscated);
    }

    @Override
    public String toString() {
      return stream()
          .map(Object::toString)
          .collect(Collectors.joining(","));
    }
  }
}
