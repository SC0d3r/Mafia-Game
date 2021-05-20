public enum ROLE {
  GOD_FATHER, MAFIA_MEMBER, DR_LACTER, MAYOR, CITIZEN, DETECTIVE, DIE_HARD, PROFESSIONAL, DR_CITY, PSYCHOLOGIST;

  public static String toString(ROLE r) {
    switch (r) {
      case GOD_FATHER:
        return "God-Father";
      case MAFIA_MEMBER:
        return "Mafia";
      case DR_LACTER:
        return "Dr.Lacter";
      case MAYOR:
        return "Mayor";
      case CITIZEN:
        return "Citizen";
      case DETECTIVE:
        return "Detective";
      case DIE_HARD:
        return "Die-Hard";
      case PROFESSIONAL:
        return "Professional";
      case PSYCHOLOGIST:
        return "Psychologist";
      case DR_CITY:
        return "DR.City";
      default:
        return "Unknown";
    }
  }
}
