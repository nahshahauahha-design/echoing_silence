package com.echoingsilence;
public class ClientSanity { public static float value = 100, target = 100; public static void tick() { value += (target - value) * 0.05f; } }
