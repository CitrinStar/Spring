package br.com.citrinstar.screenmatch.service;

public interface IConverteDados {

    <T> T obterDados(String json, Class<T> classe);
}
