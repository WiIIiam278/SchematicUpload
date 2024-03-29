/*
 * This file is part of SchematicUpload, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.schematicupload.util;

import net.william278.schematicupload.SchematicUpload;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.jetbrains.annotations.NotNull;

public interface MetricsProvider {
    int METRICS_ID = 14611;
    String WORLD_EDIT_METRIC_ID = "worldedit_type";

    // Register metrics
    default void loadMetrics() {
        Metrics metrics = new Metrics(getPlugin(), METRICS_ID);
        metrics.addCustomChart(new SimplePie(
                WORLD_EDIT_METRIC_ID,
                () -> getPlugin().getWorldEditType().getPluginName()
        ));
    }

    @NotNull
    SchematicUpload getPlugin();
}
