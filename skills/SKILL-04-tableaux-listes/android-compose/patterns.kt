package com.fram.a11y.skill04

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp

// MARK: - 1. LISTE ACCESSIBLE [A]
@Composable
fun AccessibleListPattern(users: List<User>) {
    LazyColumn(
        // Renseigne la sémantique de collection au lecteur d'écran
        modifier = Modifier.semantics {
            collectionInfo = CollectionInfo(rowCount = users.size, columnCount = 1)
        }
    ) {
        items(users.size) { index ->
            val user = users[index]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    // Indique l'index de l'item dans la collection
                    .semantics(mergeDescendants = true) {
                        collectionItemInfo = CollectionItemInfo(
                            rowIndex = index,
                            rowSpan = 1,
                            columnIndex = 0,
                            columnSpan = 1
                        )
                        // Label combiné
                        contentDescription = "${user.name}, ${user.job} chez ${user.company}"
                    }
            ) {
                Text(text = user.name)
                Text(text = "${user.job} - ${user.company}")
            }
        }
    }
}

// MARK: - 2 & 3. TABLEAU DE DONNÉES [A] & DESCRIPTION [AA]
@Composable
fun AccessibleTablePattern(employees: List<Employee>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics {
                // Description globale du tableau [AA]
                contentDescription = "Tableau des salaires des employés. ${employees.size} lignes."
                // Définition de la grille
                collectionInfo = CollectionInfo(rowCount = employees.size + 1, columnCount = 2)
            }
    ) {
        // En-têtes (Ligne 0)
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "Nom",
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        heading() // Marqué comme en-tête [A]
                        collectionItemInfo = CollectionItemInfo(rowIndex = 0, rowSpan = 1, columnIndex = 0, columnSpan = 1)
                    }
            )
            Text(
                text = "Salaire annuel",
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        heading() // Marqué comme en-tête [A]
                        collectionItemInfo = CollectionItemInfo(rowIndex = 0, rowSpan = 1, columnIndex = 1, columnSpan = 1)
                    }
            )
        }
        
        // Données
        employees.forEachIndexed { index, emp ->
            val rowIndex = index + 1
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = emp.name,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            collectionItemInfo = CollectionItemInfo(rowIndex = rowIndex, rowSpan = 1, columnIndex = 0, columnSpan = 1)
                            // Association explicite pour lecteur d'écran
                            contentDescription = "Nom: ${emp.name}"
                        }
                )
                Text(
                    text = emp.salary,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            collectionItemInfo = CollectionItemInfo(rowIndex = rowIndex, rowSpan = 1, columnIndex = 1, columnSpan = 1)
                            // Association avec ligne/colonne pour plus de clarté
                            contentDescription = "Salaire de ${emp.name}: ${emp.salary}"
                        }
                )
            }
        }
    }
}

// MARK: - 4. LISTE GROUPEE [A]
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupedListPattern(groupedUsers: Map<String, List<User>>) {
    LazyColumn {
        groupedUsers.forEach { (department, users) ->
            stickyHeader {
                Text(
                    text = department,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(8.dp)
                        .semantics { 
                            heading() // Le séparateur de groupe agit comme en-tête
                        }
                )
            }
            items(users) { user ->
                Text(
                    text = user.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

data class User(val name: String, val job: String, val company: String)
data class Employee(val name: String, val salary: String)
