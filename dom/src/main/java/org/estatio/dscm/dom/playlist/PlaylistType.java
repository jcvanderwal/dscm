package org.estatio.dscm.dom.playlist;

import org.estatio.dscm.utils.StringUtils;

public enum PlaylistType {
    MAIN,
    FILLERS;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }
}
