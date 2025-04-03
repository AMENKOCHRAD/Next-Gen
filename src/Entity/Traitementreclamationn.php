<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Entity\Reclamationn;

#[ORM\Entity]
class Traitementreclamationn
{

    #[ORM\Id]
    #[ORM\Column(type: "integer")]
    private int $id_traitement;

        #[ORM\ManyToOne(targetEntity: Reclamationn::class, inversedBy: "traitementreclamationns")]
    #[ORM\JoinColumn(name: 'reclamation_id', referencedColumnName: 'id', onDelete: 'CASCADE')]
    private Reclamationn $reclamation_id;

    #[ORM\Column(type: "integer")]
    private int $admin_id;

    #[ORM\Column(type: "datetime")]
    private \DateTimeInterface $date_prise_en_charge;

    #[ORM\Column(type: "datetime")]
    private \DateTimeInterface $date_resolution;

    #[ORM\Column(type: "text")]
    private string $commentaire;

    #[ORM\Column(type: "string", length: 255)]
    private string $type_traitement;

    #[ORM\Column(type: "string")]
    private string $statut;

    #[ORM\Column(type: "string")]
    private string $priorite;

    public function getId_traitement()
    {
        return $this->id_traitement;
    }

    public function setId_traitement($value)
    {
        $this->id_traitement = $value;
    }

    public function getReclamation_id()
    {
        return $this->reclamation_id;
    }

    public function setReclamation_id($value)
    {
        $this->reclamation_id = $value;
    }

    public function getAdmin_id()
    {
        return $this->admin_id;
    }

    public function setAdmin_id($value)
    {
        $this->admin_id = $value;
    }

    public function getDate_prise_en_charge()
    {
        return $this->date_prise_en_charge;
    }

    public function setDate_prise_en_charge($value)
    {
        $this->date_prise_en_charge = $value;
    }

    public function getDate_resolution()
    {
        return $this->date_resolution;
    }

    public function setDate_resolution($value)
    {
        $this->date_resolution = $value;
    }

    public function getCommentaire()
    {
        return $this->commentaire;
    }

    public function setCommentaire($value)
    {
        $this->commentaire = $value;
    }

    public function getType_traitement()
    {
        return $this->type_traitement;
    }

    public function setType_traitement($value)
    {
        $this->type_traitement = $value;
    }

    public function getStatut()
    {
        return $this->statut;
    }

    public function setStatut($value)
    {
        $this->statut = $value;
    }

    public function getPriorite()
    {
        return $this->priorite;
    }

    public function setPriorite($value)
    {
        $this->priorite = $value;
    }
}
