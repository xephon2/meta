package com.helger.meta.project;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;

public enum EJDK
{
  JDK6 (6),
  JDK7 (7),
  JDK8 (8);

  private final int m_nMajor;

  private EJDK (@Nonnegative final int nMajor)
  {
    m_nMajor = nMajor;
  }

  @Nonnegative
  public int getMajor ()
  {
    return m_nMajor;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return "JDK 1." + m_nMajor;
  }

  public boolean isCompatibleToRuntimeVersion (@Nonnull final EJDK eRTVersion)
  {
    return m_nMajor <= eRTVersion.m_nMajor;
  }

  public boolean isAtLeast8 ()
  {
    return m_nMajor >= 8;
  }

  @Nullable
  public static EJDK getFromMajorOrNull (final int nMajor)
  {
    return EnumHelper.findFirst (EJDK.class, e -> e.m_nMajor == nMajor);
  }
}
