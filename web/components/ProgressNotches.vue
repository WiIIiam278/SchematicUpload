<template>
  <div>

    <ol class="items-center sm:flex">
      <li v-for="index in 4" class="relative mb-6 sm:mb-0">
        <div class="flex items-center gap-6">
          <div :class="state < index-1 ? 'bg-lite-gray' : 'bg-accent'" class="z-10 flex items-center justify-center w-8 h-8 rounded-full shadow shrink-0">
            <span class="font-black text-black">{{ index }}</span>
          </div>
          <div v-if="index < 4" class="w-full bg-dark-gray h-0.5"></div>
        </div>
      </li>
    </ol>
  </div>
</template>

<script setup lang="ts">
const { state } = defineProps({
  state: {
    type: Number,
    required: true
  }
})
</script>