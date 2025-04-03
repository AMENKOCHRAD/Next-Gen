<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250403214746 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
         // 1. D'abord modifier les colonnes existantes
    $this->addSql(<<<'SQL'
    ALTER TABLE reclamationn 
    CHANGE id id INT NOT NULL, 
    CHANGE date date DATETIME NOT NULL, 
    CHANGE pieces_jointes pieces_jointes LONGTEXT NOT NULL, 
    CHANGE email email VARCHAR(255) NOT NULL
SQL);

$this->addSql(<<<'SQL'
    ALTER TABLE traitementreclamationn 
    CHANGE id_traitement id_traitement INT NOT NULL, 
    CHANGE reclamation_id reclamation_id INT DEFAULT NULL, 
    CHANGE date_prise_en_charge date_prise_en_charge DATETIME NOT NULL, 
    CHANGE date_resolution date_resolution DATETIME NOT NULL, 
    CHANGE commentaire commentaire LONGTEXT NOT NULL, 
    CHANGE type_traitement type_traitement VARCHAR(255) NOT NULL
SQL);

// 2. Vérifier si la colonne existe avant de créer l'index
if ($schema->hasTable('reclamationn')) {
    $table = $schema->getTable('reclamationn');
    
    // Ajouter la colonne si elle n'existe pas
    if (!$table->hasColumn('reclamation_id')) {
        $this->addSql('ALTER TABLE reclamationn ADD reclamation_id INT DEFAULT NULL');
    }
    
    // Supprimer l'index s'il existe
    if ($table->hasIndex('fk_reclamationn')) {
        $this->addSql('DROP INDEX fk_reclamationn ON reclamationn');
    }
    
    // Créer le nouvel index
    $this->addSql('CREATE INDEX IDX_1147AFA62D6BA2D9 ON reclamationn (reclamation_id)');
}
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql(<<<'SQL'
            DROP TABLE messenger_messages
        SQL);
        $this->addSql(<<<'SQL'
            ALTER TABLE reclamationn CHANGE id id INT AUTO_INCREMENT NOT NULL, CHANGE date date DATETIME DEFAULT 'NULL', CHANGE pieces_jointes pieces_jointes TEXT NOT NULL, CHANGE email email VARCHAR(255) DEFAULT 'NULL'
        SQL);
        $this->addSql(<<<'SQL'
            ALTER TABLE traitementreclamationn CHANGE id_traitement id_traitement INT AUTO_INCREMENT NOT NULL, CHANGE reclamation_id reclamation_id INT NOT NULL, CHANGE date_prise_en_charge date_prise_en_charge DATETIME DEFAULT 'current_timestamp()' NOT NULL, CHANGE date_resolution date_resolution DATETIME DEFAULT 'NULL', CHANGE commentaire commentaire TEXT DEFAULT NULL, CHANGE type_traitement type_traitement VARCHAR(255) DEFAULT 'NULL'
        SQL);
        $this->addSql(<<<'SQL'
            ALTER TABLE traitementreclamationn RENAME INDEX idx_1147afa62d6ba2d9 TO fk_reclamationn
        SQL);
    }
}
