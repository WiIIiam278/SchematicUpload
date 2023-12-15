<template>
  <div class="flex justify-center">
    <ClientOnly>
      <!-- TODO this breaks because babylon.js \:-(-->
      <canvas id="schematic-preview" width="500" height="500"></canvas>
    </ClientOnly>
  </div>
</template>

<script setup lang="ts">
import {renderSchematic} from "@enginehub/schematicwebviewer";

const {file} = defineProps({
  file: {
    type: File,
    required: true
  }
});

onMounted(() => {
  renderSchematic(document.querySelector('#schematic-preview'), file, {
    renderArrow: false,
    renderBars: false,
    corsBypassUrl: 'https://cors-anywhere.herokuapp.com/',
  });
});
</script>