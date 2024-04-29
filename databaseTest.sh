#!/bin/bash
# Entidades
declare -A ENTITIES
ENTITIES[usuario]="idUsuario nome endereco telefone cargo senha email cpf fkEmpresa"
ENTITIES[empresa]="idEmpresa nome cnpj logradouro email telefone matriz"
ENTITIES[maquina]="idMaquina nomeMaquina ip macAddress sistemaOperacional maxCpu maxRam maxDisco maxDispositivos fkUsuario"
ENTITIES[postagem]="idPostagem titulo conteudo tag fkUsuario"
ENTITIES[registro]="idRegistro dataHora fkMaquina consumoDisco consumoRam consumoCpu"
ENTITIES[limitador]="idLimitador fkMaquina limiteCpu limiteRam limiteDisco"
# Credenciais
USERNAME=lifeline_user
PASSWORD=urubu100
DATABASE=lifeline

CHECK_TABLE_EXISTS() {
	# atribuindo o nome da tabela iterando pela lista de entidades acima
	local TABLE_NAMES=("${!ENTITIES[@]}")
	# tabelas que não existem
	NON_EXISTENT_TABLES=()
	# tabelas que existem
	EXISTENT_TABLES=()

	# Sql queries para checar se as tabelas existem "Obs: as queries são do tipo local que é o equivalente a private para não
	# serem modificadas fora da função"
	for TABLE in "${TABLE_NAMES[@]}"; do
		local QUERY_TABLE_EXISTS=$(printf 'SHOW TABLES LIKE "%s"' "$TABLE")
		local QUERY_TABLE_IS_EMPTY=$(printf 'SELECT 1 FROM %s LIMIT 1' "$TABLE")
		echo "Checando se a tabela $TABLE existe..."
		if [[ $(mysql -u $USERNAME -p$PASSWORD -e "$QUERY_TABLE_EXISTS" $DATABASE) ]]; then
			echo "Tabela $TABLE existe"
			EXISTENT_TABLES+=("$TABLE")
		else
			echo "Tabela $TABLE não existe"
			NON_EXISTENT_TABLES+=("$TABLE")
		fi
	done
}

CREATE_NON_EXISTENT_TABLES() {
	for TABLE in "${NON_EXISTENT_TABLES[@]}"; do
		echo "$TABLE"
	done
}

CHECK_TABLES_INTEGRITY() {
	TABLES_TO_FIX=()
	for TABLE in "${EXISTENT_TABLES[@]}"; do
		local ENTITY_ATTRIBUTES=${ENTITIES[$TABLE]}
		IFS=' ' read -r -a ENTITY_ATTRIBUTES_ARRAY <<<"$ENTITY_ATTRIBUTES"
		local ATTRIBUTE_NAMES=$(mysql -u $USERNAME -p$PASSWORD -N -e "SHOW COLUMNS FROM $TABLE" $DATABASE | awk '{print $1}')
		IFS=$'\n' read -r -d '' -a TABLE_ATTRIBUTES_ARRAY <<<"$ATTRIBUTE_NAMES"
		if ! [[ "${#TABLE_ATTRIBUTES_ARRAY[@]}" -eq "${#ENTITY_ATTRIBUTES_ARRAY[@]}" ]]; then
			echo "Número de atributos da tabela $TABLE não corresponde ao número de atributos esperados."
			echo "Esperado = ${#ENTITY_ATTRIBUTES_ARRAY[@]}, sendo eles:${ENTITY_ATTRIBUTES_ARRAY[@]}"
			echo "Recebido = ${#TABLE_ATTRIBUTES_ARRAY[@]}, sendo eles:${TABLE_ATTRIBUTES_ARRAY[@]}"
			TABLES_TO_FIX+=($TABLE)
		else
			for ((i = 0; i < "${#ENTITY_ATTRIBUTES_ARRAY[@]}"; i++)); do
				if [[ $(echo " ${TABLE_ATTRIBUTES_ARRAY[$i]} " | xargs) != $(echo " ${ENTITY_ATTRIBUTES_ARRAY[$i]}" | xargs) ]]; then
					echo "Atributo da tabela $TABLE: ${TABLE_ATTRIBUTES_ARRAY[$i]} != ${ENTITY_ATTRIBUTES_ARRAY[$i]} fora do escopo encontrado"
					TABLES_TO_FIX+=($TABLE)
					break
				fi
			done
		fi
	done
	if [[ "${#TABLES_TO_FIX[@]}" -eq 0 ]]; then
		echo "Nenhum atributo fora do escopo encontrado, STATUS: OK"
	else
		echo "Atributos fora do escopo encontrados, reconfigurandos as seguintes tabelas: ${TABLES_TO_FIX[@]}"
	fi
}

CHECK_TABLE_EXISTS 2>/dev/null
if test $? -eq 0; then
	echo "As seguintes tabelas não estão configuradas: ${NON_EXISTENT_TABLES[@]}"
fi
CHECK_TABLES_INTEGRITY 2>/dev/null

CREATE_NON_EXISTENT_TABLES 2>/dev/null
